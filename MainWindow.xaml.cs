using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Runtime.InteropServices;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Effects;
using System.Windows.Threading;
using System.Windows.Interop;
using Forms = System.Windows.Forms;
using Drawing = System.Drawing;

namespace FreeRam2
{
    public partial class MainWindow : Window
    {
        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto)]
        public class MEMORYSTATUSEX
        {
            public uint dwLength;
            public uint dwMemoryLoad;
            public ulong ullTotalPhys;
            public ulong ullAvailPhys;
            public ulong ullTotalPageFile;
            public ulong ullAvailPageFile;
            public ulong ullTotalVirtual;
            public ulong ullAvailVirtual;
            public ulong ullAvailExtendedVirtual;

            public MEMORYSTATUSEX()
            {
                dwLength = (uint)Marshal.SizeOf(typeof(MEMORYSTATUSEX));
            }
        }

        [DllImport("kernel32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        public static extern bool GlobalMemoryStatusEx([In, Out] MEMORYSTATUSEX lpBuffer);

        [DllImport("kernel32.dll", SetLastError = true)]
        public static extern bool SetProcessWorkingSetSizeEx(
            IntPtr hProcess, long dwMinimumWorkingSetSize, long dwMaximumWorkingSetSize, uint Flags);

        [DllImport("user32.dll")]
        public static extern IntPtr GetForegroundWindow();

        [DllImport("user32.dll")]
        public static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint processId);

        // Tells the OS it can trim below the minimum — soft trim, no forced pageout
        private const uint QUOTA_LIMITS_HARDWS_MIN_DISABLE = 0x00000002;

        private const long TRIM_MIN = -1;
        private const long TRIM_MAX = -1;

        private static readonly HashSet<string> ProtectedProcesses = new(StringComparer.OrdinalIgnoreCase)
        {
            "csrss", "smss", "lsass", "services", "wininit", "winlogon",
            "dwm", "svchost", "system", "registry", "memcompression"
        };

        private const long MinWorkingSetBytes = 50 * 1024 * 1024;

        private static readonly Mutex SingleInstanceMutex = new(true, "Global\\FreeRam2_SingleInstance");

        private readonly DispatcherTimer _monitorTimer;
        private readonly MEMORYSTATUSEX _memStatus = new();
        private readonly SolidColorBrush _onlineBrush =
            (SolidColorBrush)new BrushConverter().ConvertFrom("#00E676");

        private readonly DropShadowEffect _glowOn = new()
        {
            Color = (Color)ColorConverter.ConvertFromString("#00E676"),
            BlurRadius = 12,
            ShadowDepth = 0
        };

        private readonly DropShadowEffect _glowOff = new()
        {
            Color = Colors.Gray,
            BlurRadius = 6,
            ShadowDepth = 0
        };

        private Forms.NotifyIcon? _trayIcon;

        private bool _isEngineEnabled = true;
        private bool _isWaitingForRamToDrop;
        private bool _isCleaning;
        private DateTime _lastCleanTime = DateTime.MinValue;

        public MainWindow()
        {
            if (!SingleInstanceMutex.WaitOne(TimeSpan.Zero, true))
            {
                MessageBox.Show("FreeRam 2.0 is already running.", "FreeRam 2.0",
                    MessageBoxButton.OK, MessageBoxImage.Information);
                Application.Current.Shutdown();
                return;
            }

            InitializeComponent();
            SetupTrayIcon();
            SetWindowIcon();

            Closing += (_, e) =>
            {
                e.Cancel = true;
                Hide();
                _trayIcon!.ShowBalloonTip(1500, "FreeRam 2.0", "Still running in the background.", Forms.ToolTipIcon.Info);
            };

            _monitorTimer = new DispatcherTimer
            {
                Interval = TimeSpan.FromSeconds(2)
            };

            _monitorTimer.Tick += MonitorTimer_Tick;
            _monitorTimer.Start();
        }

        private void SetupTrayIcon()
        {
            var contextMenu = new Forms.ContextMenuStrip();
            contextMenu.Items.Add("Show", null, (_, _) => { Show(); WindowState = WindowState.Normal; Activate(); });
            contextMenu.Items.Add("Exit", null, (_, _) => ExitApplication());

            _trayIcon = new Forms.NotifyIcon
            {
                Icon = CreateTrayIcon(),
                Text = "FreeRam 2.0",
                Visible = true,
                ContextMenuStrip = contextMenu
            };

            _trayIcon.DoubleClick += (_, _) => { Show(); WindowState = WindowState.Normal; Activate(); };
        }

        private static Drawing.Icon CreateTrayIcon()
        {
            const int size = 32;
            using var bmp = new Drawing.Bitmap(size, size);
            using var g = Drawing.Graphics.FromImage(bmp);

            g.SmoothingMode = Drawing.Drawing2D.SmoothingMode.AntiAlias;
            g.Clear(Drawing.Color.Transparent);

            using var fill = new Drawing.SolidBrush(Drawing.Color.FromArgb(0, 230, 118));
            g.FillEllipse(fill, 1, 1, size - 2, size - 2);

            using var bolt = new Drawing.SolidBrush(Drawing.Color.FromArgb(10, 10, 15));
            using var path = new Drawing.Drawing2D.GraphicsPath();
            path.AddPolygon(new[]
            {
                new Drawing.PointF(18, 4),
                new Drawing.PointF(10, 16),
                new Drawing.PointF(16, 16),
                new Drawing.PointF(14, 28),
                new Drawing.PointF(22, 16),
                new Drawing.PointF(16, 16),
            });
            g.FillPath(bolt, path);

            var handle = bmp.GetHicon();
            return Drawing.Icon.FromHandle(handle);
        }

        private void SetWindowIcon()
        {
            using var icon = CreateTrayIcon();
            Icon = Imaging.CreateBitmapSourceFromHIcon(
                icon.Handle, Int32Rect.Empty, null);
        }

        private void ExitApplication()
        {
            _monitorTimer.Stop();
            _trayIcon?.Dispose();
            SingleInstanceMutex.ReleaseMutex();
            System.Windows.Application.Current.Shutdown();
        }

        private async void MonitorTimer_Tick(object sender, EventArgs e)
        {
            if (!GlobalMemoryStatusEx(_memStatus))
                return;

            uint currentLoad = _memStatus.dwMemoryLoad;
            RamUsageText.Text = $"{currentLoad}%";

            if (StatusIndicator != null)
            {
                StatusIndicator.Fill = _isEngineEnabled ? _onlineBrush : Brushes.Gray;
                StatusIndicator.Effect = _isEngineEnabled ? _glowOn : _glowOff;
            }

            if (!_isEngineEnabled)
                return;

            if (_isWaitingForRamToDrop)
            {
                if (currentLoad <= (RecoverySlider?.Value ?? 0))
                {
                    _isWaitingForRamToDrop = false;
                    StatusText.Text = "SYSTEM REARMED";
                }
                return;
            }

            if (_isCleaning)
                return;

            if (currentLoad >= (ThresholdSlider?.Value ?? 100))
            {
                TimeSpan cooldown = TimeSpan.FromMinutes(CooldownSlider?.Value ?? 5);

                if (DateTime.Now - _lastCleanTime >= cooldown)
                {
                    await ExecuteCleanAsync();
                    _isWaitingForRamToDrop = true;
                }
                else
                {
                    TimeSpan remaining = cooldown - (DateTime.Now - _lastCleanTime);
                    StatusText.Text = $"COOLING DOWN... ({remaining.Minutes}m {remaining.Seconds}s)";
                }
            }
        }

        private async Task ExecuteCleanAsync()
        {
            if (_isCleaning)
                return;

            _isCleaning = true;
            StatusText.Text = "TRIMMING...";

            try
            {
                GetWindowThreadProcessId(GetForegroundWindow(), out uint foregroundPid);
                int myPid = Environment.ProcessId;

                var (trimmed, skipped) = await Task.Run(() =>
                {
                    int t = 0, s = 0;

                    foreach (Process proc in Process.GetProcesses())
                    {
                        try
                        {
                            if (proc.Id == myPid || proc.Id == (int)foregroundPid || proc.Id == 0)
                            {
                                s++;
                                continue;
                            }

                            if (ProtectedProcesses.Contains(proc.ProcessName))
                            {
                                s++;
                                continue;
                            }

                            if (proc.WorkingSet64 < MinWorkingSetBytes)
                            {
                                s++;
                                continue;
                            }

                            SetProcessWorkingSetSizeEx(
                                proc.Handle, TRIM_MIN, TRIM_MAX, QUOTA_LIMITS_HARDWS_MIN_DISABLE);
                            t++;
                        }
                        catch
                        {
                            s++;
                        }
                        finally
                        {
                            proc.Dispose();
                        }
                    }

                    return (t, s);
                });

                _lastCleanTime = DateTime.Now;
                StatusText.Text = $"TRIMMED {trimmed} PROCESSES ({skipped} SKIPPED)";
            }
            catch (Exception ex)
            {
                StatusText.Text = "ENGINE ERROR: CHECK PERMISSIONS";
                Debug.WriteLine($"Purge Error: {ex.Message}");
            }
            finally
            {
                _isCleaning = false;
            }
        }

        private void TitleBar_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            if (e.LeftButton == MouseButtonState.Pressed)
                DragMove();
        }

        private void Close_Click(object sender, RoutedEventArgs e)
            => ExitApplication();

        private void Minimize_Click(object sender, RoutedEventArgs e)
        {
            Hide();
            _trayIcon?.ShowBalloonTip(1500, "FreeRam 2.0", "Still running in the background.", Forms.ToolTipIcon.Info);
        }

        private void ToggleEngine()
        {
            _isEngineEnabled = !_isEngineEnabled;

            if (StatusIndicator != null)
            {
                StatusIndicator.Fill = _isEngineEnabled ? _onlineBrush : Brushes.Gray;
                StatusIndicator.Effect = _isEngineEnabled ? _glowOn : _glowOff;
            }

            if (EngineToggleButton?.Template.FindName("toggleBorder", EngineToggleButton) is Border border
                && EngineToggleButton.Template.FindName("toggleText", EngineToggleButton) is TextBlock text)
            {
                border.Background = _isEngineEnabled
                    ? new SolidColorBrush((Color)ColorConverter.ConvertFromString("#00E676"))
                    : new SolidColorBrush((Color)ColorConverter.ConvertFromString("#FF5252"));
                text.Text = _isEngineEnabled ? "ON" : "OFF";
            }

            StatusText.Text = _isEngineEnabled ? "SYSTEM ONLINE" : "ENGINE STANDBY";
        }

        private void EngineToggle_Click(object sender, MouseButtonEventArgs e)
            => ToggleEngine();

        private void EngineToggleButton_Click(object sender, RoutedEventArgs e)
            => ToggleEngine();

        private async void CleanNowButton_Click(object sender, RoutedEventArgs e)
        {
            await ExecuteCleanAsync();

            if (_isEngineEnabled)
                _isWaitingForRamToDrop = true;
        }

        private void ThresholdSlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            if (ThresholdText != null)
                ThresholdText.Text = $"{(int)e.NewValue}%";

            if (RecoverySlider != null && RecoverySlider.Value >= e.NewValue)
                RecoverySlider.Value = (int)e.NewValue - 1;
        }

        private void RecoverySlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            if (RecoveryText != null)
                RecoveryText.Text = $"{(int)e.NewValue}%";

            if (ThresholdSlider != null && e.NewValue >= ThresholdSlider.Value)
                RecoverySlider.Value = (int)ThresholdSlider.Value - 1;
        }

        private void CooldownSlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            if (CooldownText != null)
                CooldownText.Text = $"{(int)e.NewValue}m";
        }
    }
}
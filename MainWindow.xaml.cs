using System;
using System.Diagnostics;
using System.Runtime.InteropServices;
using System.Windows;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Threading;

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

        [DllImport("ntdll.dll")]
        public static extern int NtSetSystemInformation(int infoClass, IntPtr info, int length);

        private const int SystemMemoryListInformation = 80;

        // ⭐ ZERO SSD IMPACT COMMANDS
        private const int MemoryPurgeLowPriorityStandbyList = 2;
        private const int MemoryPurgeStandbyList = 4;

        private readonly DispatcherTimer _monitorTimer;
        private readonly SolidColorBrush _onlineBrush =
            (SolidColorBrush)new BrushConverter().ConvertFrom("#00E676");

        private bool _isEngineEnabled = true;
        private bool _isWaitingForRamToDrop;
        private bool _didLowPriorityPass = false;   // ⭐ stage tracker
        private DateTime _lastCleanTime = DateTime.MinValue;

        public MainWindow()
        {
            InitializeComponent();

            _monitorTimer = new DispatcherTimer
            {
                Interval = TimeSpan.FromSeconds(2)
            };

            _monitorTimer.Tick += MonitorTimer_Tick;
            _monitorTimer.Start();
        }

        private void MonitorTimer_Tick(object sender, EventArgs e)
        {
            var memStatus = new MEMORYSTATUSEX();

            if (!GlobalMemoryStatusEx(memStatus))
                return;

            uint currentLoad = memStatus.dwMemoryLoad;
            RamUsageText.Text = $"{currentLoad}%";

            if (StatusIndicator != null)
                StatusIndicator.Fill = _isEngineEnabled ? _onlineBrush : Brushes.Gray;

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

            if (currentLoad >= (ThresholdSlider?.Value ?? 100))
            {
                TimeSpan cooldown = TimeSpan.FromMinutes(CooldownSlider?.Value ?? 5);

                if (DateTime.Now - _lastCleanTime >= cooldown)
                {
                    ExecuteClean();
                    _isWaitingForRamToDrop = true;
                }
                else
                {
                    TimeSpan remaining = cooldown - (DateTime.Now - _lastCleanTime);
                    StatusText.Text = $"COOLING DOWN... ({remaining.Minutes}m {remaining.Seconds}s)";
                }
            }
        }

        private void ExecuteClean()
        {
            try
            {
                // ⭐ Stage 1: Low priority standby
                // ⭐ Stage 2: Full standby
                int command = !_didLowPriorityPass
                    ? MemoryPurgeLowPriorityStandbyList
                    : MemoryPurgeStandbyList;

                IntPtr commandPtr = Marshal.AllocHGlobal(sizeof(int));
                Marshal.WriteInt32(commandPtr, command);

                int result = NtSetSystemInformation(
                    SystemMemoryListInformation,
                    commandPtr,
                    sizeof(int));

                Marshal.FreeHGlobal(commandPtr);

                _lastCleanTime = DateTime.Now;

                if (result == 0)
                {
                    if (!_didLowPriorityPass)
                    {
                        _didLowPriorityPass = true;
                        StatusText.Text = "LOW PRIORITY CACHE PURGED";
                    }
                    else
                    {
                        _didLowPriorityPass = false;
                        StatusText.Text = "STANDBY CACHE FULLY CLEARED";
                    }
                }
                else
                {
                    StatusText.Text = "SYSTEM BUSY: RETRYING IN NEXT CYCLE";
                }
            }
            catch (Exception ex)
            {
                StatusText.Text = "ENGINE ERROR: CHECK PERMISSIONS";
                Debug.WriteLine($"Purge Error: {ex.Message}");
            }
        }

        private void TitleBar_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            if (e.LeftButton == MouseButtonState.Pressed)
                DragMove();
        }

        private void Close_Click(object sender, RoutedEventArgs e)
            => Application.Current.Shutdown();

        private void Minimize_Click(object sender, RoutedEventArgs e)
            => WindowState = WindowState.Minimized;

        private void EngineToggle_Click(object sender, MouseButtonEventArgs e)
        {
            _isEngineEnabled = !_isEngineEnabled;
            StatusText.Text = _isEngineEnabled ? "SYSTEM ONLINE" : "ENGINE STANDBY";
        }

        private void CleanNowButton_Click(object sender, RoutedEventArgs e)
        {
            StatusText.Text = "MANUAL OVERRIDE...";
            ExecuteClean();

            if (_isEngineEnabled)
                _isWaitingForRamToDrop = true;
        }

        private void ThresholdSlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            if (ThresholdText != null)
                ThresholdText.Text = $"{(int)e.NewValue}%";
        }

        private void RecoverySlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            if (RecoveryText != null)
                RecoveryText.Text = $"{(int)e.NewValue}%";
        }

        private void CooldownSlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            if (CooldownText != null)
                CooldownText.Text = $"{(int)e.NewValue}m";
        }
    }
}
package etc;
import java.util.zip. *;
import java.io. *;

public class unzip {
    public void unzip(String Path, String Dir) {
        File dir = new File(Dir);
        if (!dir.exists()) 
            dir.mkdirs();
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(Path);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(Dir + File.separator + fileName);
                System
                    .out
                    .println("Unzipping to " + newFile.getAbsolutePath());
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File fi = new File("C:\\FreeRam\\rammap\\RAMMap.zip");
        fi.delete();
    }
}

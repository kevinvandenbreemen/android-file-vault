package com.vandenbreemen.mobilesecurestorage.android.mvp.sfsactions;

import com.vandenbreemen.mobilesecurestorage.file.api.FileType;
import com.vandenbreemen.mobilesecurestorage.security.crypto.persistence.SecureFileSystem;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kevin
 */
public class FileListInteractor {

    private SecureFileSystem sfs;

    FileListInteractor(SecureFileSystem sfs) {
        this.sfs = sfs;
    }

    public List<FileListItemView> getFileList() {
        return sfs.listFiles().stream().map((item) -> {

            FileType fileType = null;
            try {
                fileType = sfs.getDetails(item).getFileType();
            } catch (Exception cex) {

            }

            return new FileListItemView(item,
                    fileType
            );
        }).collect(Collectors.toList());
    }

    public List<FileListItemView> sortByName(boolean asc) {

        Comparator<FileListItemView> comp = (o1, o2) -> {
            if (asc) {
                return o1.name.compareTo(o2.name);
            }
            return o2.name.compareTo(o1.name);
        };

        return sfs.listFiles().stream().map((item) -> {

            FileType fileType = null;
            try {
                fileType = sfs.getDetails(item).getFileType();
            } catch (Exception cex) {

            }

            return new FileListItemView(item,
                    fileType
            );
        }).sorted(comp).collect(Collectors.toList());
    }

}

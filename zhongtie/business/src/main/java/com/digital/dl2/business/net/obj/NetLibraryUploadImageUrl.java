package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/9/26.
 */
public class NetLibraryUploadImageUrl {
    private boolean status;
    private int error_code;
    private String message;
    private String image_url;

    @Override
    public String toString() {
        return "NetLibraryUploadImageUrl{" +
                "status=" + status +
                ", error_code=" + error_code +
                ", message='" + message + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}

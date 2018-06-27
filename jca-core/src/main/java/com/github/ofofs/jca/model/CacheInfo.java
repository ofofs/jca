package com.github.ofofs.jca.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 缓存信息
 *
 * @author kangyonggan
 * @since 2018/6/26 0026
 */
public class CacheInfo implements Serializable {

    /**
     * 缓存的值
     */
    private Object value;

    /**
     * 过期时间
     */
    private Long expire;

    /**
     * 最后更新时间
     */
    private Date updateDate;

    public CacheInfo(Object value, Long expire) {
        this.value = value;
        this.expire = expire;
        this.updateDate = new Date();
    }

    public Object getValue() {
        // 每次取值时更新最后更新时间
        this.updateDate = new Date();
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 判断缓存是否过期
     *
     * @return 过期返回true、否则返回false
     */
    public boolean isExpire() {
        if (expire == -1) {
            return false;
        }

        if (System.currentTimeMillis() < updateDate.getTime() + expire) {
            return false;
        }

        return true;
    }
}

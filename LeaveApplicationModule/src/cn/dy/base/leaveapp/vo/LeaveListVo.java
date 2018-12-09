package cn.dy.base.leaveapp.vo;

import cn.dy.base.leaveapp.domain.LeaveApplication;

import java.util.Date;

/**
 * @ Author     ：hyy.
 * @ Date       ：Created in 16:19 2018/11/30
 * @ Description：${description}
 * @ Modified By：hyy.
 * @Version: $version$
 */
public class LeaveListVo extends LeaveApplication {

    private static final long serialVersionUID = -8532343226547783944L;
    private Date audited_time;

    public Date getAudited_time() {
        return audited_time;
    }

    public void setAudited_time(Date audited_time) {
        this.audited_time = audited_time;
    }
}

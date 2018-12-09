package cn.dy.base.leaveapp.service;

/**
 * @ Author     ：hyy.
 * @ Date       ：Created in 14:58 2018/11/27
 * @ Description：${description}
 * @ Modified By：hyy.
 * @Version: $version$
 */
import cn.dy.base.framework.esb.def.ESBAnnotation;
import cn.dy.base.framework.esb.def.RepMessage;
import cn.dy.base.leaveapp.domain.LeaveApplication;

import java.util.Date;
import java.util.HashMap;

public interface LeaveManageService {
    @ESBAnnotation(
            names = {"name", "leaveApplication"},
            req_login = false
    )
    RepMessage createNewLeaveApplication(String name, LeaveApplication leaveApplication);
    @ESBAnnotation(
            names = {"flow_type", "flow_sts", "application_id", "create_id" },//"create_time",增审批流程时修改"cur_detail_id"
            req_login = false
    )
    RepMessage createNewLeaveAuditingFlow(String flow_type, String flow_sts, long application_id, long create_id);
    @ESBAnnotation(
            names = {"audit_ids", "flow_id"},//"flow_id", "pre_id","create_time", "audit_time", "audit_remark", "audit_sts"
            /**
             *
            "flow_id":,//流程ID
            //"flow_id", "pre_id",上一节点ID "audit_id",审核人 , "audit_sts"
            */
            req_login = false//long flow_id, long pre_id, long audit_id, long audit_time, String audit_remark, String audit_sts
    )
    RepMessage createNewLeaveAuditingDetail(HashMap<String,Object> audit_id, long flow_id);
    @ESBAnnotation(
            names={"leave_id"},
            req_login = false
    )
    RepMessage getLeave(long leave_id);
    @ESBAnnotation(
            names={"leave_type", "sts", "begin_time", "end_time", "pageNum", "pageSize"},
            req_login = false
    )
    RepMessage queryLeaveList(String leave_type, String sts, Date begin_time, Date end_time, long pageNum, long pageSize);
    @ESBAnnotation(
            names = {"leave_type", "sts", "begin_time", "end_time", "query", "pageNum", "pageSize"},
            req_login = false
    )
    RepMessage queryApproveLeaveList(String leave_type, String sts, Date begin_time, Date end_time, String query, long pageNum, long pageSize);
    @ESBAnnotation(
            names = {"userId"},
            req_login = false
    )
    RepMessage getLeaveDetail();
    class LeaveManageParam{
        private long leave_id;
        private String leave_type;
        private Date begin_time;
        private Date end_time;
        private String query;
        private String sts;
        private long pageNum;
        private long pageSize;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public long getLeave_id() {
            return leave_id;
        }

        public void setLeave_id(long leave_id) {
            this.leave_id = leave_id;
        }

        public long getPageNum() {
            return pageNum;
        }

        public void setPageNum(long pageNum) {
            this.pageNum = pageNum;
        }

        public long getPageSize() {
            return pageSize;
        }

        public void setPageSize(long pageSize) {
            this.pageSize = pageSize;
        }

        public String getLeave_type() {
            return leave_type;
        }

        public void setLeave_type(String leave_type) {
            this.leave_type = leave_type;
        }

        public Date getBegin_time() {
            return begin_time;
        }

        public void setBegin_time(Date begin_time) {
            this.begin_time = begin_time;
        }

        public Date getEnd_time() {
            return end_time;
        }

        public void setEnd_time(Date end_time) {
            this.end_time = end_time;
        }

        public String getSts() {
            return sts;
        }

        public void setSts(String sts) {
            this.sts = sts;
        }
    }
    class LeaveAuditParam{
        private long detail_id;
        private long flow_id;
        private long pre_id;
        private long audit_id;
        private String flow_type;
        private String flow_sts;
        private long application_id;
        private long create_id;
        private long cur_detail_id;
        private long audit_time;
        private String audit_remark;
        private String audit_sts;

        public long getDetail_id() {
            return detail_id;
        }

        public void setDetail_id(long detail_id) {
            this.detail_id = detail_id;
        }

        public long getFlow_id() {
            return flow_id;
        }

        public void setFlow_id(long flow_id) {
            this.flow_id = flow_id;
        }

        public long getPre_id() {
            return pre_id;
        }

        public void setPre_id(long pre_id) {
            this.pre_id = pre_id;
        }

        public long getAudit_id() {
            return audit_id;
        }

        public void setAudit_id(long audit_id) {
            this.audit_id = audit_id;
        }

        public String getFlow_type() {
            return flow_type;
        }

        public void setFlow_type(String flow_type) {
            this.flow_type = flow_type;
        }

        public String getFlow_sts() {
            return flow_sts;
        }

        public void setFlow_sts(String flow_sts) {
            this.flow_sts = flow_sts;
        }

        public long getApplication_id() {
            return application_id;
        }

        public void setApplication_id(long application_id) {
            this.application_id = application_id;
        }

        public long getCreate_id() {
            return create_id;
        }

        public void setCreate_id(long create_id) {
            this.create_id = create_id;
        }

        public long getCur_detail_id() {
            return cur_detail_id;
        }

        public void setCur_detail_id(long cur_detail_id) {
            this.cur_detail_id = cur_detail_id;
        }

        public long getAudit_time() {
            return audit_time;
        }

        public void setAudit_time(long audit_time) {
            this.audit_time = audit_time;
        }

        public String getAudit_remark() {
            return audit_remark;
        }

        public void setAudit_remark(String audit_remark) {
            this.audit_remark = audit_remark;
        }

        public String getAudit_sts() {
            return audit_sts;
        }

        public void setAudit_sts(String audit_sts) {
            this.audit_sts = audit_sts;
        }
    }
}

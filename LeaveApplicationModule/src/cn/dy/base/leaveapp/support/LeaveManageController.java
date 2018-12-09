package cn.dy.base.leaveapp.support;

import cn.dy.base.framework.esb.def.RepMessage;
import cn.dy.base.framework.esb.def.ServiceBusinessException;
import cn.dy.base.leaveapp.dao.LeaveManageDao;
import cn.dy.base.leaveapp.domain.LeaveApplication;
import cn.dy.base.leaveapp.service.LeaveManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ Author     ：hyy.
 * @ Date       ：Created in 18:56 2018/11/27
 * @ Description：${description}
 * @ Modified By：hyy.
 * @Version: $version$
 */
public class LeaveManageController implements LeaveManageService {
    protected static Logger logger = LoggerFactory.getLogger(LeaveManageController.class);
    private LeaveManageDao leaveManageDao;

    public LeaveManageController() {
        if (this.leaveManageDao == null) {
            this.leaveManageDao = new LeaveManageDao();
        }
    }

    //private AuthorityDao authorityDao;//权限控制
    public RepMessage getLeave(long leave_id) {
        LeaveManageParam param = new LeaveManageParam();
        param.setLeave_id(leave_id);

        RepMessage repMessage = new RepMessage();
        try {
            Map<String, Object> map = new HashMap();
            map.put("result", this.leaveManageDao.getLeave(param));
            repMessage.setContent(map);
            repMessage.setResponse_code("0");
        } catch (Exception var10) {
            repMessage.setResponse_code("-1");
            repMessage.setResponse_desc("条件查询请假列表失败");
            repMessage.setResponse_detail(var10.getLocalizedMessage());
            logger.warn("条件查询请假列表失败");
        }
        return repMessage;
    }

    @Override
    public RepMessage queryLeaveList(String leave_type, String sts, Date begin_time, Date end_time, long pageNum, long pageSize) {
        LeaveManageParam param = new LeaveManageParam();
        param.setLeave_type(leave_type);
        param.setSts(sts);
        param.setBegin_time(begin_time);
        param.setEnd_time(end_time);
        param.setPageNum(pageNum);
        param.setPageSize(pageSize);

        RepMessage repMessage = new RepMessage();
        try {
            Map<String, Object> map = new HashMap();
            map.put("result", this.leaveManageDao.getLeaves(param));
            repMessage.setContent(map);
            repMessage.setResponse_code("0");
        } catch (Exception var10) {
            repMessage.setResponse_code("-1");
            repMessage.setResponse_desc("条件查询请假列表失败");
            Map<String,Object> map = new HashMap<>();
            map.put("err",var10.getMessage());
            repMessage.setContent(map);
            repMessage.setResponse_detail(var10.getLocalizedMessage());
            logger.warn("条件查询请假列表失败");
        }
        return repMessage;
    }

    /**
     * 新增请假流程
     * @param flow_type
     * @param flow_sts
     * @param application_id
     * @param create_id
     * @return
     */
    @Override
    public RepMessage createNewLeaveAuditingFlow(String flow_type, String flow_sts, long application_id, long create_id) {
        LeaveAuditParam param = new LeaveAuditParam();
        param.setFlow_type(flow_type);
        param.setFlow_sts(flow_sts);
        param.setApplication_id(application_id);
        param.setCreate_id(create_id);

        RepMessage repMessage = new RepMessage();
        try {
            Map<String, Object> map = new HashMap();
            map.put("result", this.leaveManageDao.createNewAuditingFlow(param));
            repMessage.setContent(map);
            repMessage.setResponse_code("0");
        } catch (Exception var10) {
            repMessage.setResponse_code("-1");
            repMessage.setResponse_desc("新增请假审批流程失败");
            repMessage.setResponse_detail(var10.getLocalizedMessage());
            logger.warn("新增请假审批流程失败");
        }
        return repMessage;
    }

    /**
     * 新增请假详情
     * @param audit_ids
     * @return
     */
    @Override
    public RepMessage createNewLeaveAuditingDetail(HashMap<String, Object> audit_ids, long flow_id) {


        RepMessage repMessage = new RepMessage();
        try {
            Map<String, Object> map = new HashMap();
            map.put("result", this.leaveManageDao.createNewDetail(audit_ids, flow_id));
            repMessage.setContent(map);
            repMessage.setResponse_code("0");
        } catch (Exception var10) {
            repMessage.setResponse_code("-1");
            repMessage.setResponse_desc("新增请假流程详情失败");
            repMessage.setResponse_detail(var10.getLocalizedMessage());
            logger.warn("新增请假流程详情失败");
        }
        return repMessage;
    }

    /**
     * 审批列表
     * @param leave_type
     * @param sts
     * @param begin_time
     * @param end_time
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public RepMessage queryApproveLeaveList(String leave_type, String sts, Date begin_time, Date end_time, String query, long pageNum, long pageSize) {
        LeaveManageParam param = new LeaveManageParam();
        param.setLeave_type(leave_type);
        param.setSts(sts);
        param.setBegin_time(begin_time);
        param.setEnd_time(end_time);
        param.setQuery(query);
        param.setPageNum(pageNum);
        param.setPageSize(pageSize);

        RepMessage repMessage = new RepMessage();
        try {
            Map<String, Object> map = new HashMap();
            map.put("result", this.leaveManageDao.getApproveLeaves(param));
            repMessage.setContent(map);
            repMessage.setResponse_code("0");
        } catch (Exception var10) {
            repMessage.setResponse_code("-1");
            repMessage.setResponse_desc("条件查询审批列表失败");
            repMessage.setResponse_detail(var10.getLocalizedMessage());
            logger.warn("条件查询审批列表失败");
        }
        return repMessage;
    }

    @Override
    public RepMessage getLeaveDetail() {
        return null;
    }

    @Override
    public RepMessage createNewLeaveApplication(final String name, final LeaveApplication leaveApplication) {
        RepMessage repMessage = new RepMessage();
        try {
            Map<String, Object> map = new HashMap();
            map.put("result", this.leaveManageDao.createNewLeave(name, leaveApplication));
            repMessage.setContent(map);
            repMessage.setResponse_code("0");
        }catch (ServiceBusinessException e){
            repMessage.setResponse_code("-1");
            repMessage.setResponse_desc("已申请过此类假条");
            repMessage.setResponse_detail("已申请过此类假条");
            repMessage.setResponse_detail(e.getLocalizedMessage());
            logger.warn("已申请过此类假条");
        }
        catch (Exception var10) {
            repMessage.setResponse_code("-1");
            repMessage.setResponse_desc("新增请假申请失败");
            repMessage.setResponse_detail(var10.getLocalizedMessage());
            logger.warn("新增请假申请失败");
        }
        return repMessage;
    }
}

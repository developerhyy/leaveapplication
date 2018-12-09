package cn.dy.base.leaveapp.dao;

import cn.dy.base.framework.db.DBConnectPool;
import cn.dy.base.framework.db.DBUtil;
import cn.dy.base.framework.esb.call.imp.JsonServiceProxy;
import cn.dy.base.framework.esb.def.RepMessage;
import cn.dy.base.framework.esb.def.ServiceBusinessException;
import cn.dy.base.leaveapp.common.Page;
import cn.dy.base.leaveapp.domain.AuditDetail;
import cn.dy.base.leaveapp.domain.AuditFlow;
import cn.dy.base.leaveapp.domain.LeaveApplication;
import cn.dy.base.leaveapp.domain.OperateLogRequest;
import cn.dy.base.leaveapp.service.LeaveManageService;
import cn.dy.base.leaveapp.util.SystemUtil;
import cn.dy.base.leaveapp.util.Tools;
import cn.dy.base.leaveapp.vo.LeaveListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ Author     ：hyy.
 * @ Date       ：Created in 19:01 2018/11/27
 * @ Description：${description}
 * @ Modified By：hyy.
 * @Version: $version$
 */
public class LeaveManageDao {
    private TransactionTemplate transactionTemplate;//事务
    private DataSource dataSource = DBConnectPool.create().getDataSource();
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public LeaveManageDao() {
        if (this.transactionTemplate == null) {
            DataSourceTransactionManager tm = new DataSourceTransactionManager(dataSource);
            this.transactionTemplate = new TransactionTemplate(tm);
        }
        if (this.namedParameterJdbcTemplate == null) {
            this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
        }
    }

    public long checkLeaveApplication(String name, LeaveApplication leaveApplication) {
        //TODO 加入验证名字name与userid是否匹配
        Map<String, Object> parameters = new HashMap();
        parameters.put("leave_type", leaveApplication.getLeave_type());
        parameters.put("apply_id", leaveApplication.getApply_id());
        parameters.put("sts", leaveApplication.getSts());
        long name_num = this.namedParameterJdbcTemplate.queryForLong(DBUtil.getCountSql("select duration from bz_leave_application where leave_type=:leave_type and apply_id=:apply_id and sts=:sts"), parameters);
        return name_num;
    }
    public long getUniqueId(String seqName) {
        return Tools.getSequesce(seqName, this.dataSource);
    }
    public void addLeave(LeaveApplication leaveApplication) {
        Map<String, Object> parameters = DBUtil.getMapObj(leaveApplication);
        this.namedParameterJdbcTemplate.update("insert into bz_leave_application\n  (id, leave_type, begin_time, end_time, duration, reason, apply_id, create_time, sts)\nvalues\n  (:id, :leave_type, :begin_time, :end_time, :duration, :reason, :apply_id, :create_time, :sts)", parameters);
    }
    public LeaveApplication getLeave(LeaveManageService.LeaveManageParam param) {
        if(param.getLeave_id() == 0) throw new RuntimeException("参数未赋值");//初始化默认值0
        List<LeaveApplication> list = this.queryLeaveApplication(param);
        return list != null && list.size() > 0 ? (LeaveApplication) list.get(0) : null;
    }

    public List<LeaveApplication> queryLeaveApplication(LeaveManageService.LeaveManageParam param) {
        Map<String, Object> params = new LinkedHashMap();
        StringBuffer sbSql = new StringBuffer("SELECT id, leave_type, begin_time, end_time, duration, reason, apply_id, create_time, sts FROM `bz_leave_application` where 1=1 ");
        String sSql = this.createParameters(param, params, sbSql);
        RowMapper<LeaveApplication> rowMapper = new RowMapper<LeaveApplication>() {
            public LeaveApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
                LeaveApplication leaveApplication = new LeaveApplication();
                leaveApplication.setId(rs.getLong("id"));
                leaveApplication.setLeave_type(rs.getString("leave_type"));
                leaveApplication.setDuration(rs.getLong("duration"));
                leaveApplication.setReason(rs.getString("reason"));
                return leaveApplication;
            }
        };
        return  this.namedParameterJdbcTemplate.query(sSql, params, rowMapper);
    }
    private String createParameters(LeaveManageService.LeaveManageParam param, Map<String, Object> params, StringBuffer sbSql) {
        if (param != null) {
            long id = param.getLeave_id();
            if (id > 0) {
                sbSql.append(" and id = :leave_id");
                params.put("leave_id", id);
            }
            String sts = param.getSts();
            if(StringUtils.isNotEmpty(sts)){
                sbSql.append(" and sts = :sts");
                params.put("sts", sts);
            }

            String leave_type = param.getLeave_type();
            if (StringUtils.isNotEmpty(leave_type)) {
                sbSql.append(" and leave_type = :leave_type");
                params.put("leave_type", leave_type);
            }

        }
        sbSql.append(" order by id ");
        return sbSql.toString();
    }

    public Long createNewLeave(final String name,final LeaveApplication leaveApplication){
        return (Long) this.transactionTemplate.execute(new TransactionCallback<Long>() {
            public Long doInTransaction(TransactionStatus status) {
                if (leaveApplication.getApply_id() <= 0) {
                    //TODO 获取当前用户id
                    leaveApplication.setApply_id(2);
                }
                long id;
                if (checkLeaveApplication(name, leaveApplication) > 0L) {
                    throw new ServiceBusinessException("已申请过此类假条","已申请过此类假条");
                } else {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    id = getUniqueId("SEQ_BZ_LEAVE_APPLICATION");
                    leaveApplication.setId(id);
                    leaveApplication.setCreate_time(Tools.parseDate((df.format(new Date()))));
                    leaveApplication.setSts("0");//状态:0初始1审批中2结束3删除
                    addLeave(leaveApplication);
                    /*if (functions != null && functions.length > 0) {
                        String[] var7 = functions;
                        int var6 = functions.length;

                        for(int var5 = 0; var5 < var6; ++var5) {
                            String func_id = var7[var5];
                            RoleControl.this.authorityDao.addRoleAuthority(role.getId(), func_id);
                        }
                    }*/

                    /*OperationControl operationControl = new OperationControl();
                    Operation operation = new Operation();
                    operation.module = "cn.dy.base.system";
                    operation.child_module = "cn.dy.base.system.Role";
                    operation.operation_obj = "角色";
                    if (SystemModule.context != null) {
                        try {
                            operation.staff_id = Integer.parseInt(SystemModule.context.getContextByName("staff_id").toString());
                            operation.corp_id = Integer.parseInt(SystemModule.context.getContextByName("corp_id").toString());
                        } catch (Exception var8) {
                            ;
                        }
                    }

                    operation.operation_name = "创建角色";
                    operation.operate_content = "操作员[" + operation.staff_id + "]创建角色[" + role.getName() + "]";
                    operationControl.writeOperationLog(operation);*/
                    return id;
                }
            }
        });
    }
    public Object getLeaves(final LeaveManageService.LeaveManageParam param){
        return (Page)this.transactionTemplate.execute(new TransactionCallback<Page<LeaveListVo>>() {
            public Page<LeaveListVo> doInTransaction(TransactionStatus status) {
                StringBuffer sbSql = new StringBuffer("SELECT id, leave_type, begin_time, end_time, create_time, null audited_time,sts FROM `bz_leave_application` where 1=1 \n ");
                Map<String, Object> params = new LinkedHashMap();
                String sSql = createParameters(param, params, sbSql);
                Page<LeaveListVo> page = new Page();
                page.setTotal_count(countOfLeaves(param, sSql, params));
                page.setRecords(queryLeaves(param, sSql, params));
                return page;
            }
        });
    }

    public Object getApproveLeaves(final LeaveManageService.LeaveManageParam param){
        return (Page)this.transactionTemplate.execute(new TransactionCallback<Page<LeaveListVo>>() {
            public Page<LeaveListVo> doInTransaction(TransactionStatus status) {
                StringBuffer sbSql = new StringBuffer("SELECT id, leave_type, begin_time, end_time, create_time, null audited_time,sts FROM `bz_leave_application` where 1=1 \n ");
                Map<String, Object> params = new LinkedHashMap();
                String sSql = createParameters(param, params, sbSql);
                Page<LeaveListVo> page = new Page();
                page.setTotal_count(countOfLeaves(param, sSql, params));
                page.setRecords(queryLeaves(param, sSql, params));
                return page;
            }
        });
    }

    public List<LeaveListVo> queryLeaves(LeaveManageService.LeaveManageParam param, String sSql, Map<String, Object> params) {
        RowMapper<LeaveListVo> rowMapper = new RowMapper<LeaveListVo>() {
            public LeaveListVo mapRow(ResultSet rs, int rowNum) throws SQLException {

                LeaveListVo leaveListVo = new LeaveListVo();
                leaveListVo.setId(rs.getLong("id"));
                leaveListVo.setLeave_type(rs.getString("leave_type"));

                leaveListVo.setBegin_time(Tools.parseDate(rs.getString("begin_time")));
                leaveListVo.setEnd_time(Tools.parseDate(rs.getString("end_time")));
                leaveListVo.setCreate_time(Tools.parseDate((rs.getString("create_time"))));
                leaveListVo.setAudited_time(rs.getDate("audited_time") != null ? Tools.parseDate(rs.getString("audited_time")) : null);
                leaveListVo.setSts(rs.getString("sts"));
                return leaveListVo;
            }
        };
        int iPageNum = (int)param.getPageNum(); //? NumberUtils.toInt((String)fields.get("pageNum"), 0) : 0;
        int iPerPageSize = (int)param.getPageSize(); //fields != null ? NumberUtils.toInt((String)fields.get("pageSize"), 0) : 0;
        return iPageNum != 0 && iPerPageSize != 0 ? this.namedParameterJdbcTemplate.query(DBUtil.getPagingSql(sSql), DBUtil.parsePagingParam(params, iPageNum, iPerPageSize), rowMapper) : this.namedParameterJdbcTemplate.query(sSql, params, rowMapper);
    }
    public long countOfLeaves(LeaveManageService.LeaveManageParam param, String sSql, Map<String, Object> params) {
        return this.namedParameterJdbcTemplate.queryForLong(DBUtil.getCountSql(sSql), params);
    }

    public Object createNewAuditingFlow(final LeaveManageService.LeaveAuditParam param) {
        //验证
        if(param.getFlow_type() !=AuditFlow.AuditFlowType.LEAVEAUDITFLOWTYPE.toString() || param.getFlow_sts() != AuditFlow.FlowSts.Auditing.toString())
            throw new ServiceBusinessException("创建流程参数不正确！", "参数错误");
        return (Long) this.transactionTemplate.execute(new TransactionCallback<Long>() {
            public Long doInTransaction(TransactionStatus status) {
                boolean isNew = false;
                AuditFlow auditFlow = null;
                if(param.getFlow_id() > 0){
                    auditFlow = getAuditFlow(param.getDetail_id());//修改
                } else{

                    auditFlow = new AuditFlow();//新建
                    auditFlow.setId(getUniqueId("SEQ_BZ_AUDITING_FLOW"));
                    isNew = true;
                }
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                auditFlow.setFlow_type(param.getFlow_type());
                auditFlow.setFlow_sts(param.getFlow_sts());
                auditFlow.setApplication_id(param.getApplication_id());//请假单id
                auditFlow.setCreate_id(param.getCreate_id());//申请人id
                auditFlow.setCreate_time(Tools.parseDate((df.format(new Date()))));
                int i = addAuditFlow(auditFlow);
                /*if (functions != null && functions.length > 0) {
                    String[] var7 = functions;
                    int var6 = functions.length;

                    for(int var5 = 0; var5 < var6; ++var5) {
                        String func_id = var7[var5];
                        RoleControl.this.authorityDao.addRoleAuthority(role.getId(), func_id);
                    }
                }*/

                /*OperationControl operationControl = new OperationControl();
                Operation operation = new Operation();
                operation.module = "cn.dy.base.system";
                operation.child_module = "cn.dy.base.system.Role";
                operation.operation_obj = "角色";
                if (SystemModule.context != null) {
                    try {
                        operation.staff_id = Integer.parseInt(SystemModule.context.getContextByName("staff_id").toString());
                        operation.corp_id = Integer.parseInt(SystemModule.context.getContextByName("corp_id").toString());
                    } catch (Exception var8) {
                        ;
                    }
                }

                operation.operation_name = "创建角色";
                operation.operate_content = "操作员[" + operation.staff_id + "]创建角色[" + role.getName() + "]";
                operationControl.writeOperationLog(operation);*/
                SystemUtil.addOperateLogRequest("新增审核流程", "新增审核流程id[" + auditFlow.getId() + "]信息", "请假管理模块", "请假审核模块", "审核管理");;

                return auditFlow.getId();

            }
        });
    }

    private int addAuditFlow(AuditFlow auditFlow) {
        Map<String, Object> parameters = DBUtil.getMapObj(auditFlow);
        return this.namedParameterJdbcTemplate.update("insert into bz_leave_application\n  (id, flow_id, pre_id, audit_id, audit_sts, create_time)\nvalues\n  (:id, :flow_id, :pre_id, :audit_id, :audit_sts, :create_time)", parameters);
    }

    public AuditDetail getAuditDetail(long detail_id){
        Map<String, Object> parameters = new HashMap();
        parameters.put("id", detail_id);
        AuditDetail auditDetail = this.namedParameterJdbcTemplate.queryForObject(DBUtil.getCountSql("select * from bz_auditing_detail where id=:id "), parameters, AuditDetail.class);
        return auditDetail;
    }
    public AuditFlow getAuditFlow(long detail_id){
        Map<String, Object> parameters = new HashMap();
        parameters.put("id", detail_id);
        AuditFlow auditFlow = this.namedParameterJdbcTemplate.queryForObject(DBUtil.getCountSql("select * from bz_auditing_flow where id=:id "), parameters, AuditFlow.class);
        return auditFlow;
    }
    public Object createNewDetail(HashMap<String, Object> map, long flow_id){

        AuditDetail auditDetail = null;
        LeaveManageService.LeaveAuditParam param =null;
        for (int i=0;i<map.size();i++){
            if(i==0 && auditDetail == null){
                param = new LeaveManageService.LeaveAuditParam();
                param.setFlow_id(flow_id);
                param.setPre_id(0);
                param.setAudit_id((long)map.get((i+1)+""));
                auditDetail = createNewAuditingDetail(param);
            }
            if(StringUtils.isEmpty(map.get((i+1)).toString())) break;
            param.setFlow_id(flow_id);
            param.setPre_id(auditDetail.getId());
            param.setAudit_id((long)map.get((i+1)+""));
            auditDetail = createNewAuditingDetail(param);
        }
        return "success";
    }

    public AuditDetail createNewAuditingDetail(final LeaveManageService.LeaveAuditParam param) {
        //验证
        if(param.getFlow_id() <= 0 )
            throw new ServiceBusinessException("未创建请假审核流程", "未创建请假审核流程");
        if(param.getAudit_id() <= 0 )
            throw new ServiceBusinessException("未指定审核人员", "未指定审核人员");
        if(param.getFlow_sts() != AuditDetail.AuditDetailSts.PassAudit.toString())
            throw new ServiceBusinessException("非待审批的审核详情", "非待审批的审核详情");
        return (AuditDetail) this.transactionTemplate.execute(new TransactionCallback<AuditDetail>() {
            public AuditDetail doInTransaction(TransactionStatus status) {
                AuditDetail auditDetail = null;
                boolean isNew=false;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                if(param.getDetail_id() > 0){
                    auditDetail = getAuditDetail(param.getDetail_id());//修改
                } else{
                    auditDetail = new AuditDetail();//新建
                    auditDetail.setId(getUniqueId("SEQ_BZ_AUDITING_DETAIL"));
                    isNew = true;
                }
                auditDetail.setFlow_id(param.getFlow_id());
                if(param.getPre_id() > 0){
                    auditDetail.setPre_id(param.getPre_id());
                }
                else{
                    auditDetail.setPre_id(0);//为第一级审核详情
                }
                auditDetail.setAudit_id(param.getAudit_id());
                auditDetail.setAudit_sts(param.getAudit_sts());//
                //审核日期,审核备注在审核成功处，赋值
                auditDetail.setCreate_time(Tools.parseDate((df.format(new Date()))));
                long i = addAuditDetail(isNew, auditDetail);
                if(i<=0) throw new ServiceBusinessException("更新失败", "更新失败");
                if(isNew)
                    SystemUtil.addOperateLogRequest("新增审核明细", "新增审核明细id[" + auditDetail.getId() + "]信息", "请假管理模块", "请假审核模块", "审核管理");
                else
                    SystemUtil.addOperateLogRequest("修改审核明细", "修改审核明细id[" + auditDetail.getId() + "]信息", "请假管理模块", "请假审核模块", "审核管理");
                return auditDetail;
            }
        });
    }

    private long addAuditDetail(boolean isNew, AuditDetail auditDetail) {
        Map<String, Object> parameters = DBUtil.getMapObj(auditDetail);
        if(isNew)
            return this.namedParameterJdbcTemplate.update("insert into bz_leave_application\n  (id, flow_id, pre_id, audit_id, audit_sts, create_time)\nvalues\n  (:id, :flow_id, :pre_id, :audit_id, :audit_sts, :create_time)", parameters);
        else
            return this.namedParameterJdbcTemplate.update("update bz_leave_application\n  set(flow_id=:flow_id, pre_id=:pre_id, audit_id=:audit_id, audit_sts=:audit_sts, create_time=:create_time)\n where \n  id=:id ", parameters);//audit_time=:audit_time, audit_remark=:audit_remark,
    }
}

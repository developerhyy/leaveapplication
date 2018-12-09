package cn.dy.base.leaveapp.support;

import cn.dy.base.framework.db.DBConnectPool;
import cn.dy.base.framework.db.util.DBExecuteUtil;
import cn.dy.base.framework.esb.def.RepMessage;
import cn.dy.base.leaveapp.common.Page;
import cn.dy.base.leaveapp.dao.MemberDao;
import cn.dy.base.leaveapp.domain.MemberInfo;
import cn.dy.base.leaveapp.domain.ZTreeNode;
import cn.dy.base.leaveapp.service.MemberService;
import cn.dy.base.leaveapp.util.GZTreeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.*;

/**
 * @ Author     ：hyy.
 * @ Date       ：Created in 14:15 2018/11/29
 * @ Description：${description}
 * @ Modified By：hyy.
 * @Version: $version$
 */
public class MemberServiceController implements MemberService {
    protected static Logger logger = LoggerFactory.getLogger(MemberServiceController.class);
    private MemberDao memberDao = null;

    public MemberServiceController() {
        if (this.memberDao == null) {
            this.memberDao = new MemberDao();
        }
    }

    @Override
    public RepMessage queryMemberList(String dept, String query, long pageNum, long pageSize) {
        MemberServiceParam param = new MemberServiceParam();
        param.setDept(dept);
        param.setQuery(query);
        param.setPageNum(pageNum);
        param.setPageSize(pageSize);

        RepMessage repMessage = new RepMessage();
        try {
            Map<String, Object> map = new HashMap();
            map.put("result", this.memberDao.getMember(param));
            repMessage.setContent(map);
            repMessage.setResponse_code("0");
        } catch (Exception var10) {
            repMessage.setResponse_code("-1");
            repMessage.setResponse_desc("条件查询人员失败");
            repMessage.setResponse_detail(var10.getLocalizedMessage());
            logger.warn("条件查询人员失败");
        }
        return repMessage;
    }

    @Override
    public List getDeptTree(boolean all) {
        DBExecuteUtil db = new DBExecuteUtil();
        List result = new ArrayList();
        String sql = " SELECT name,id,order_index,parent_id,sts, (CASE WHEN group_type='NORMAL' AND parent_id='101' THEN 'group' ELSE 'branch' END ) AS dep  FROM ecc_contact_group ";
        String sql2 = "select i.name,CONCAT('c',i.ID)id,h.order_index,h.group_id as parent_id,i.sts from ecc_contact_holder as h,ecc_contact_group as g,ecc_contact_info as i ";
        if (!all) {
            sql = sql + "WHERE sts='1' ORDER BY dep in('branch')";
        }

        sql2 = sql2 + "where g.id=h.group_id and i.id=h.contact_id and i.sts='1' ";
        List nodes = db.queryList(sql, ZTreeNode.class);
        List nodes2 = db.queryList(sql2, ZTreeNode.class);
        ((List)nodes).addAll(nodes2);
        Map filterMap = new HashMap();
        if (!filterMap.isEmpty()) {
            List newResult = new ArrayList();
            Iterator iterator = ((List)nodes).iterator();

            while(iterator.hasNext()) {
                ZTreeNode node = (ZTreeNode)iterator.next();
                if (filterMap != null) {
                    newResult.add(node);
                }
            }

            nodes = newResult;
        }

        String cropName = this.memberDao.getCorpName();
        GZTreeUtil.createZTree(cropName, result, (List)nodes, "101", (Map)null);
        return result;
    }


}

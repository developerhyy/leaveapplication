package cn.dy.base.leaveapp.test;

import cn.dy.base.framework.esb.def.RepMessage;
import cn.dy.base.leaveapp.common.Page;
import cn.dy.base.leaveapp.domain.LeaveApplication;
import cn.dy.base.leaveapp.domain.MemberInfo;
import cn.dy.base.leaveapp.service.LeaveManageService;
import cn.dy.base.leaveapp.service.MemberService;
import cn.dy.base.leaveapp.service.SeniorityService;
import cn.dy.base.leaveapp.support.LeaveManageController;
import cn.dy.base.leaveapp.support.MemberServiceController;
import cn.dy.base.leaveapp.support.SeniorityServiceImpl;
import cn.dy.base.leaveapp.vo.LeaveListVo;
import cn.dy.base.leaveapp.vo.MemberInfoVo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ Author     ：hyy.
 * @ Date       ：Created in 17:08 2018/11/28
 * @ Description：${description}
 * @ Modified By：hyy.
 * @Version: $version$
 */
public class Test {
    public static void main(String[] args) {
        System.out.println("========================");
        /*LeaveManageService leaveManageService = new LeaveManageController();

        //leaveManageService.getLeave(1);
        LeaveApplication leaveApplication = new LeaveApplication();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        leaveApplication.setLeave_type("病假");
        leaveApplication.setApply_id(3);//申请人
        leaveApplication.setSts("0");
        try {
            leaveApplication.setBegin_time(df.parse(df.format(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        leaveApplication.setEnd_time(new java.sql.Date(new Date().getTime()));
        leaveApplication.setReason("henmji");
        leaveManageService.createNewLeaveApplication("xxxxx",leaveApplication);

        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        System.out.println(new Date()+"+++++++" + new java.sql.Date(System.currentTimeMillis()));*/


       /* MemberService memberService = new MemberServiceController();
        RepMessage repMessage = memberService.queryMemberList("","郑晗炜",0,10);
List list = memberService.getDeptTree(true);
        //System.out.println(repMessage.getContent().get("result").toString());
        Page page = (Page)repMessage.getContent().get("result");
        List<MemberInfo> memberInfo = page.getRecords();
        System.out.println(memberInfo.get(0).getName());
System.out.println(list);*/

        /*LeaveManageService leaveManageService = new LeaveManageController();
        RepMessage repMessage = leaveManageService.queryLeaveList("","",null,null,1,4);
        Page<LeaveListVo> page = (Page)repMessage.getContent().get("result");
        List<LeaveListVo> list = page.getRecords();
        for (LeaveListVo l:list
             ) {

            System.out.println(l.getCreate_time());
        }
        System.out.println(repMessage.getContent().get("result").toString());*/

        SeniorityService seniorityService = new SeniorityServiceImpl();
        RepMessage repMessage = seniorityService.getLeftFurloughDays("1");
        System.out.println(repMessage.getContent().get("result"));
        //RepMessage repMessage = seniorityService.querySeniority("","","",1,10);
        /*Page<MemberInfoVo> page = (Page)repMessage.getContent().get("result");
        List<MemberInfoVo> list = page.getRecords();
        for (MemberInfoVo l:list
                ) {

            System.out.println(l.getName());
        }*/
    }
}

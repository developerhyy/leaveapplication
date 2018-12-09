<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
%>
<!DOCTYPE html>
<html class="no-js">
<head>
    <jsp:include page="../assets/common_inc_new.jsp" flush="true"></jsp:include>
    <script type="text/javascript">
        var user_id = loginInfo.staff_id;
    </script>
    <link type="text/css" rel="stylesheet" href="${home}/scripts/fancybox/jquery.fancybox.css"/>
    <script type="text/javascript" src="${home}/scripts/fancybox/jquery.fancybox.pack.js"></script>
    <script type="text/javascript" src="../system/scripts/zDialog.js"></script>
    <script type="text/javascript" src="../system/zDialog.js"></script>


    <title>请假详情</title>
    <style>
        .checkModule{
            border-radius: 20px;
            width: 20px;
            height: 20px;
            align-content: center;
        }
    </style>
</head>

<body class="mainbody">
<form  name = "form1" id="form1" method="post" enctype="multipart/form- data">
    <input name="imgfile" type="file" id="imgfile" size="40"  style="border: 1px solid #d4d4d4;display:none;" onchange="viewmypic(this.form.imgfile);" />
</form>
<form method="post" id="infoForm">
    <!--导航栏-->
    <div class="location">
        <a onclick="doReturn();" class="back"><i></i><span>返回管理页面</span></a>
        <i class="arrow"></i>
        <span id="lbTitle">请假详情</span>
    </div>
    <input type="hidden" id="lib_template_id" value= "1" />
    <input type="hidden" id="lib_url" value="template_1.jsp">
    <div class="tab-content" style="padding-bottom: 50px;">
        <dl>
            <dt>请假类型：</dt>
            <dd>
                <label id="leavetype">事假</label>
            </dd>
        </dl>
        <dl>
            <dt>开始时间：</dt>
            <dd>
                <label id="begintime">2018-11-07 12:12</label>
            </dd>
        </dl>
        <dl>
            <dt>结束时间：</dt>
            <dd>
                <label id="endtime">2018-11-07 12:12</label>
            </dd>
        </dl>
        <dl>
            <dt>时长：</dt>
            <dd>
                <label id="day">2天</label>
            </dd>
        </dl>

        <dl>
            <dt>请假事由：</dt>
            <dd>
                <label id="reason">外出办理相关证件</label>
            </dd>
        </dl>
        <dl>
            <dt>审批人员</dt>
            <dd>
                <input type="button" name="approver1" id="approver1" for="approver1" class="input normal" sucmsg=" " />
                <span class="Validform_checktip">*必填</span>
            </dd>
            <dd>
                <input type="button" name="btnAddapprover" value="增加审批人员" class="btn" onclick="alert('新增人员');" />
            </dd>
        </dl>
    </div>
</form>


        <!--工具栏-->
        <div class="page-footer">
            <div class="btn-list">
                <input type="button" name="btnBackoff" value="撤回" id="btnSubmit" class="btn" onclick="doCommit();"/>
                <input type="button" name="btnReturn" value="返回上一级" class="btn yellow" onclick="doReturn();" />
            </div>
            <div class="clear"></div>
        </div>



        <script type="text/javascript" charset="utf-8" src="${home}/ueditor/ueditor.config.js"></script>
        <script type="text/javascript" charset="utf-8" src="${home}/ueditor/ueditor.all.min.js"> </script>
        <script type="text/javascript" charset="utf-8" src="${home}/ueditor/lang/zh-cn/zh-cn.js"></script>
        <link type="text/css" rel="stylesheet" href="${home}/system/common/css/star.css"/>
        <script src="${home}/system/common/scripts/star.js" type="text/javascript"></script>
        <script type="text/javascript" src="js/worker_info.js"></script>
        <script type="text/javascript">
            var worker_type = "";
            var wz_id="";
            var nowDateTime = "";
        </script>
</body>
</html>

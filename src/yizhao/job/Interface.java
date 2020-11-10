package yizhao.job;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.interfaces.schedule.BaseCronJob;
import yizhao.Service.K3_InvokeSevice;
public class Interface extends BaseCronJob {

    BaseBean bean = new BaseBean();
    private String Action_Name = "客户对接";
    @Override
    public void execute() {
    try {
        JSONObject modelJson = new JSONObject();
        //实体主键，自定义需要发送的参数
        modelJson.put("FName", "customerfullname");
        modelJson.put("FINVOICETITLE", "paymentaccountname");
        modelJson.put("FINVOICEBANKNAME", "paymentbankname");
        modelJson.put("FINVOICEBANKACCOUNT", "paymentaccountid");
        modelJson.put("FINVOICETEL", "contactphone");
        modelJson.put("FINVOICEADDRESS", "registeredaddress");

        modelJson.put("FEntity", modelJson);

        bean.writeLog(Action_Name + " modelJson:" + modelJson);
        String result = InterK3(modelJson);//
        bean.writeLog("result"+result);
        if (!"".equals(result)) {
            return ;
        }
        bean.writeLog("*************** " + Action_Name + " End ***************");
    }catch (Exception e){
        e.printStackTrace();
        bean.writeLog("*************** " + Action_Name + " Exception:" + e.getMessage());
    }

    }
    public String  InterK3(JSONObject modelJson) throws Exception {
        //对接K3
        String k3_server_url = "http://124.70.180.251:1169/k3cloud/";
        String k3_databaseId = "5f8e9eff2aa426";
        String k3_username = "Administrator";
        String k3_password = "888888";
      //  int k3_langStr =2053;
        //    int k3_lang = Util.getIntValue(k3_langStr);
        bean.writeLog(Action_Name + " k3_server_url:" + k3_server_url);
        bean.writeLog(Action_Name + " k3_databaseId:" + k3_databaseId);
        bean.writeLog(Action_Name + " k3_username:" + k3_username);
        bean.writeLog(Action_Name + " k3_password:" + k3_password);
        //  bean.writeLog(Action_Name + " k3_lang:" + k3_lang);
        if ("".equals(k3_server_url)){
            return "请设置K3服务器地址";
        }
        if ("".equals(k3_databaseId)){
            return "请设置K3账套信息";
        }
        if ("".equals(k3_username)){
            return "请设置K3登录账号";
        }
        if ("".equals(k3_password)){
            return "请设置K3登录密码";
        }
       /* if (k3_lang < 0){
            k3_lang = 2052;
        }*/
        String k3_customer_formId ="k3_customer_formId" ;
        bean.writeLog(Action_Name + " k3_customer_formId:" + k3_customer_formId);
        if ("".equals(k3_customer_formId)){
            return "请设置K3客户表单ID";
        }
        JSONObject dataJson = new JSONObject();
        String k3_voucher_creator = "k3_voucher_creator";
        bean.writeLog(Action_Name + " k3_voucher_creator:" + k3_voucher_creator);
        dataJson.put("Creator", k3_voucher_creator);
        dataJson.put("NeedUpDateFields", new JSONArray());
        dataJson.put("NeedReturnFields", new JSONArray());
        dataJson.put("IsDeleteEntry", "true");
        dataJson.put("SubSystemId", "");
        dataJson.put("IsVerifyBaseDataField", "false");
        dataJson.put("IsEntryBatchFill", "true");
        dataJson.put("ValidateFlag", "true");
        dataJson.put("NumberSearch", "true");
        dataJson.put("InterationFlags", "");
        dataJson.put("IsAutoSubmitAndAudit", "false");
        dataJson.put("Model", modelJson.toString());
        bean.writeLog(Action_Name + " Save paramJson：" + dataJson.toString());

    if(K3_InvokeSevice.Login(k3_server_url, k3_databaseId, k3_username, k3_password)){
        String sResult = K3_InvokeSevice.Save(k3_server_url, k3_customer_formId, dataJson.toString());
        bean.writeLog(Action_Name + " Save result：" + sResult);
        JSONObject sResultJson = JSONObject.fromObject(sResult);
        JSONObject sResultJsonInfo = (JSONObject) sResultJson.get("Result");
        JSONObject responseStatus = (JSONObject) sResultJsonInfo.get("ResponseStatus");
        Boolean IsSuccess = responseStatus.getBoolean("IsSuccess");
        if (IsSuccess){
            RecordSet rs = new RecordSet();
            String customerid = sResultJsonInfo.getString("Id");
            String k3code = sResultJsonInfo.getString("Number");
            //审核操作
            JSONObject submitJson = new JSONObject();
            submitJson.put("CreateOrgId", 0);
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(k3code);
            submitJson.put("Numbers", jsonArray);
            submitJson.put("Ids", "");
            submitJson.put("InterationFlags", "");
            submitJson.put("NetworkCtrl", "");
        }else {
            JSONArray Errors = responseStatus.getJSONArray("Errors");
            String FieldName = Errors.getJSONObject(0).getString("FieldName");
            String Message = Errors.getJSONObject(0).getString("Message");
            this.bean.writeLog(Action_Name + "，FieldName：" + FieldName + " Message：" + Message);
            return Message;
        }
        return "";
    }
    else {
        bean.writeLog(Action_Name + " 我到这里啦：" + "结束啦");
        return "登录失败";
    }
    }
    }


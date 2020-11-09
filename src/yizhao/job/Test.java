package yizhao.job;

import com.sun.mail.imap.protocol.ID;
import freemarker.ext.beans.NumberModel;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;

import weaver.general.Util;

import weaver.interfaces.schedule.BaseCronJob;

import javax.print.attribute.standard.JobName;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test extends BaseCronJob{
    BaseBean logBean = new BaseBean();

    private final static String JobName = "取数据库数据";

    public void execute(){
        logBean.writeLog("--------------------" + JobName +" Begin---------------------------");
        //将数据存入map
        List<Map<String, Object>> result= Savemap();
        JSONArray jsonArray = JSONArray.fromObject(result);
        logBean.writeLog(jsonArray);
    }

       public void  Test1() {//测试获取数据并打印
        RecordSet rs = new RecordSet();
        //获取数据
        // String configKey = "马超";
        //  String configSql = "select * from hrmresource where LASTNAME = '" + configKey + "'";
        String configSql = "select * from hrmresource";
        rs.execute(configSql);
        while (rs.next()) {
            String lastname = Util.null2String(rs.getString("lastname"));
            String password = Util.null2String(rs.getString("password"));
            logBean.writeLog("lastname：：：" + lastname);
            logBean.writeLog("password：：：" + password);
   }

}
    public List<Map<String, Object>> Savemap(){
        RecordSet rs = new RecordSet();
        String configSql = "select * from hrmresource";
        rs.execute(configSql);
        Map<String,Object> res= null;
        List<Map<String,Object>> list =new ArrayList<Map<String,Object>>();
        while (rs.next()){
         res  = new HashMap<String,Object>();
         res.put("id",rs.getString("id"));
         res.put("loginid",rs.getString("loginid"));
         res.put("password",rs.getString("password"));
         res.put("lastname",rs.getString("lastname"));
         list.add(res);
        }
        return list;
    }

}

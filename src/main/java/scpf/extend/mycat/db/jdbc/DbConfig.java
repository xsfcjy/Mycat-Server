package scpf.extend.mycat.db.jdbc;


import java.io.IOException;  
import java.io.InputStream;  
import java.util.Properties;  

/** 
* 数据库配置文件读取方法 
* @author WANGYAN 
* 
*/  
public class DbConfig {  
    
 private String driver;  
 private String url;  
 private String userName;  
 private String password;  
    
 public DbConfig() {  
     InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("dbConfig.properties");
     Properties p=new Properties();
     try {  
         p.load(inputStream);  
         this.driver=p.getProperty("driver");  
         this.url=p.getProperty("url");  
         this.userName=p.getProperty("username");  
         this.password=p.getProperty("passwrod");  
     } catch (IOException e) {  
         // TODO Auto-generated catch block  
         e.printStackTrace();  
     }  
        
 }  
    
 public String getDriver() {  
     return driver;  
 }  
 public String getUrl() {  
     return url;  
 }  
 public String getUserName() {  
     return userName;  
 }  
 public String getPassword() {  
     return password;  
 }  
    
    

} 
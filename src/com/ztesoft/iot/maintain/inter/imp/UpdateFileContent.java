package com.ztesoft.iot.maintain.inter.imp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URLDecoder;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ztesoft.iot.maintain.dao.DbOperate;
import com.ztesoft.iot.maintain.inter.ServletInter;
import com.ztesoft.iot.maintain.readconfig.ReadCon;
import com.ztesoft.iot.maintain.utils.ExecuteCommand;

import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.exception.TaskExecFailException;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import net.neoremind.sshxcute.task.impl.ExecShellScript;
import net.sf.json.JSONObject;

public class UpdateFileContent implements ServletInter {
	private HttpServletResponse response;
	private HttpServletRequest request;
	private DbOperate dbo = new DbOperate();

	public UpdateFileContent(HttpServletResponse response, HttpServletRequest request) {
		this.response = response;
		this.request = request;
	}

	@Override
	public void doService() {
		JSONObject json = new JSONObject();
		StringBuffer content = null;
		try {
			String host = request.getParameter("host");
			String app = request.getParameter("app");
			String port = request.getParameter("port");
			String file = request.getParameter("file");
			String time = request.getParameter("time");
			String newFilecontent = request.getParameter("new_filecontent");
			
			if (newFilecontent.equals(new String(newFilecontent.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
				newFilecontent = new String(newFilecontent.getBytes("ISO-8859-1"), "UTF-8");
			} 
			
			newFilecontent = newFilecontent.replaceAll("&lt;","<").replaceAll("&gt;",">");
			//将该内容生成文件，然后上传到linux系统中：
			createFile(file, newFilecontent);
			
			SSHExec ssh = ExecuteCommand.connect(host, ReadCon.USERNAME, ReadCon.PASSWORD);
			
			ssh.uploadSingleDataToServer(filenameTemp,"/home/wangyj/");
			
			List<Object[]> list = dbo.search("select file_content from monitor_file_log where host_ip = ? and app_name = ? and host_port = ? and file_name = ? and file_change_date = to_date(?,'yyyy-mm-dd hh24:mi:ss')", host, app, port, file, time);
			Clob clob = null;
			if(list.size() > 0){
				clob = (Clob)list.get(0)[0];
			}
			if(clob != null){
				content = clobToString(clob);
				json.put("code", "0000");
				json.put("content", content.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\r\n", "<br>"));
			}else{
				json.put("code", "1000");
				json.put("message", "数据空！");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			json.put("code", "1000");
			json.put("message", "请求数据异常！");
			e1.printStackTrace();
		}
		try {
			response.setHeader("Content-Type", "text/html; charset=utf-8");
			PrintWriter pw = response.getWriter();
			pw.write(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public StringBuffer clobToString(Clob clob) throws SQLException, IOException {
		Reader is = clob.getCharacterStream();
		BufferedReader br = new BufferedReader(is);
		StringBuffer sb = new StringBuffer();
		String s = null;
		while ((s = br.readLine()) != null) {
			sb.append(s).append("\r\n");
		}
		br.close();
		is.close();
		return sb;
	}
	
	
	//生成文件路径
    private static String path = ReadCon.UPLOADTEMPFILEDIR;
    
    //文件路径+名称
    private static String filenameTemp;
    /**
     * 创建文件
     * @param fileName  文件名称
     * @param filecontent   文件内容
     * @return  是否创建成功，成功则返回true
     */
    public static boolean createFile(String fileName,String filecontent){
        Boolean bool = false;
        filenameTemp = path+fileName;//文件路径+名称+文件类型
        File file = new File(filenameTemp);
        try {
			// 如果文件存在，则删除文件
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			System.out.println("success create file,the file is " + filenameTemp);
			// 创建文件成功后，写入内容到文件里
			writeFileContent(filenameTemp, filecontent);
			bool = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }
    
    /**
     * 向文件中写入内容
     * @param filepath 文件路径与名称
     * @param newstr  写入的内容
     * @return
     * @throws IOException
     */
    public static boolean writeFileContent(String filepath,String newstr) throws IOException{
		String[] lines = newstr.split("<br>");
		FileWriter fw = new FileWriter(filepath);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0; i < lines.length; i++) {
			bw.write(lines[i] + "\r\n");
		}
		bw.close();
    	fw.close();
    	
    	Boolean bool = false;
       /* String filein = newstr+"\r\n";//新写入的行，换行
        String temp  = "";
        
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos  = null;
        PrintWriter pw = null;
        try {
            File file = new File(filepath);//文件路径(包括文件名称)
            //将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();
            
            //文件原有内容
            for(int i=0;(temp =br.readLine())!=null;i++){
                buffer.append(temp);
                // 行与行之间的分隔符 相当于“\n”
                buffer = buffer.append(System.getProperty("line.separator"));
            }
            buffer.append(filein);
            
            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
            bool = true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally {
            //不要忘记关闭
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }*/
        return bool;
    }
    
    /**
     * 删除文件
     * @param fileName 文件名称
     * @return
     */
    public static boolean delFile(String fileName){
        Boolean bool = false;
        filenameTemp = path+fileName;
        File file  = new File(filenameTemp);
        try {
            if(file.exists()){
                file.delete();
                bool = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }
    
    public static void main(String[] args) {
    	
    	SSHExec ssh = ExecuteCommand.connect("192.168.25.15", ReadCon.USERNAME, ReadCon.PASSWORD);
		System.out.println("----"+"");
		System.out.println("----"+ReadCon.USERNAME);
		System.out.println("----"+ReadCon.PASSWORD);
		CustomTask ct3 = new ExecCommand("sed -i 's/\r$//' /home/wangyj/sshxcute_test.sh ");
		CustomTask ct2 = new ExecShellScript("/home/wangyj", "./sshxcute_test.sh", "hello world");
		// 执行脚本并且返回一个Result对象
		Result res;
		try {
			ssh.exec(ct3);
			res = ssh.exec(ct2);
			// 检查执行结果，如果执行成功打印输出，如果执行失败，打印错误信息
			if (res.isSuccess) {
				System.out.println("Return code: " + res.rc);
				System.out.println("sysout: " + res.sysout);
			} else {
				System.err.println("Return code: " + res.rc);
				System.err.println("error message: " + res.error_msg);
			}
		} catch (TaskExecFailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

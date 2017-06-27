import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.hssf.util.CellRangeAddress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zd.core.service.BaseService;
import com.zd.school.jw.arrangecourse.service.JwCourseteacherService;
import com.zd.school.jw.eduresources.model.JwTGradeclass;
import com.zd.school.jw.eduresources.service.JwTGradeclassService;
import com.zd.school.plartform.system.model.SysUser;
import com.zd.school.plartform.system.service.SysUserService;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring.xml" })
public class aas {

	@Resource
	JwTGradeclassService thisService;
    @Resource
    private SysUserService userService;
	/*
	@Test
	public final void unitTest() throws IOException {
	
    	String sql="select project  from CW_T_FINANCIALBOOKITEM where FINANCIALBOOK_ID='9d7bae00-e240-409e-9a77-81c9a295178a'  group by project ";
    	List<Object[]> ysxms=thisService.ObjectQuerySql(sql);
    	List<SysUser> list=new ArrayList<>();
    	for (int i = 0; i < ysxms.size(); i++) {
			String ysxm=ysxms.get(i)+"";
			sql="select abstracts,expend,project  from CW_T_FINANCIALBOOKITEM where expend is not null and project='"+ysxm+"' and FINANCIALBOOK_ID='9d7bae00-e240-409e-9a77-81c9a295178a'";
			double sum=0;
			List<Object[]> lists=thisService.ObjectQuerySql(sql);
			for (int j = 0; j < lists.size(); j++) {
				Object[] o=lists.get(j);
				SysUser u=new SysUser();
				u.setExtField01(o[0]+"");
				u.setExtField02(o[1]+"");
				u.setExtField03(o[2]+"");
				list.add(u);
				double je;
				try {
					je=new Double(o[1]+"");
				} catch (Exception e) {
					je=0;
				}
				sum+=je;
			}
			SysUser u=new SysUser();
			u.setExtField01("小计");
			u.setExtField02(sum+"");
			u.setExtField03("");
			list.add(u);
		}
    	for (SysUser sysUser : list) {
    		System.out.println(sysUser.getExtField01()+"\t"+sysUser.getExtField02()+"\t"+sysUser.getExtField03());
		}
    	
	}
*/
}

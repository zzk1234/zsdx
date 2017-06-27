import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zd.core.util.DBContextHolder;
import com.zd.school.plartform.system.service.SysUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring.xml" })
public class bbs {
	
	@Resource
    private SysUserService userService;
	/*
	@Test
	public final void unitTest() throws IOException {
		DBContextHolder.setDBType(DBContextHolder.DATA_SOURCE_Three);
		System.out.println(userService.doQueryAll().size());
	}*/

}

package org.jeecg.modules.demo.test.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.util.superSearch.ObjectParseUtil;
import org.jeecg.common.util.superSearch.QueryRuleEnum;
import org.jeecg.common.util.superSearch.QueryRuleVo;
import org.jeecg.modules.demo.test.entity.JeecgDemo;
import org.jeecg.modules.demo.test.service.IJeecgDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @Title: Controller
 * @Description: 测试demo 
 * @author： jeecg-boot 
 * @date： 2018-12-29 
 * @version：V1.0
 */
@RestController
@RequestMapping("/test/jeecgDemo")
@Slf4j
public class JeecgDemoController {
	@Autowired
	private IJeecgDemoService jeecgDemoService;

	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 分页列表查询
	 * 
	 * @param jeecgDemo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */

	@ApiOperation(value = "获取Demo数据列表", notes = "获取所有Demo数据列表", produces = "application/json")
	@GetMapping(value = "/list")
	public Result<IPage<JeecgDemo>> list(JeecgDemo jeecgDemo, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		Result<IPage<JeecgDemo>> result = new Result<IPage<JeecgDemo>>();
		QueryWrapper<JeecgDemo> queryWrapper = null;
		//================================================================================
		//高级组合查询
		try {
			String superQueryParams = req.getParameter("superQueryParams");
			if(oConvertUtils.isNotEmpty(superQueryParams)) {
				// 解码
				superQueryParams = URLDecoder.decode(superQueryParams, "UTF-8");
				List<QueryRuleVo> userList = JSON.parseArray(superQueryParams, QueryRuleVo.class);
				log.info(superQueryParams);
				queryWrapper = new QueryWrapper<JeecgDemo>();
				for (QueryRuleVo rule : userList) {
					if(oConvertUtils.isNotEmpty(rule.getField()) && oConvertUtils.isNotEmpty(rule.getRule()) && oConvertUtils.isNotEmpty(rule.getVal())){
						ObjectParseUtil.addCriteria(queryWrapper, rule.getField(), QueryRuleEnum.getByValue(rule.getRule()), rule.getVal());
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//================================================================================

		// 手工转换实体驼峰字段为下划线分隔表字段
		queryWrapper = queryWrapper==null?new QueryWrapper<JeecgDemo>(jeecgDemo):queryWrapper;
		Page<JeecgDemo> page = new Page<JeecgDemo>(pageNo, pageSize);
		
		// 排序逻辑 处理
		String column = req.getParameter("column");
		String order = req.getParameter("order");
		if (oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
			if ("asc".equals(order)) {
				queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
			} else {
				queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
			}
		}
		
		IPage<JeecgDemo> pageList = jeecgDemoService.page(page, queryWrapper);
//		log.info("查询当前页：" + pageList.getCurrent());
//		log.info("查询当前页数量：" + pageList.getSize());
//		log.info("查询结果数量：" + pageList.getRecords().size());
//		log.info("数据总数：" + pageList.getTotal());
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	 * 添加
	 * 
	 * @param jeecgDemo
	 * @return
	 */
	@PostMapping(value = "/add")
	@AutoLog(value = "添加测试DEMO")
	public Result<JeecgDemo> add(@RequestBody JeecgDemo jeecgDemo) {
		Result<JeecgDemo> result = new Result<JeecgDemo>();
		try {
			jeecgDemoService.save(jeecgDemo);
			result.success("添加成功！");
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			result.error500("操作失败");
		}
		return result;
	}

	/**
	 * 编辑
	 * 
	 * @param jeecgDemo
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<JeecgDemo> eidt(@RequestBody JeecgDemo jeecgDemo) {
		Result<JeecgDemo> result = new Result<JeecgDemo>();
		JeecgDemo jeecgDemoEntity = jeecgDemoService.getById(jeecgDemo.getId());
		if (jeecgDemoEntity == null) {
			result.error500("未找到对应实体");
		} else {
			boolean ok = jeecgDemoService.updateById(jeecgDemo);
			// TODO 返回false说明什么？
			if (ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}

	/**
	 * 通过id删除
	 * 
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除测试DEMO")
	@DeleteMapping(value = "/delete")
	public Result<JeecgDemo> delete(@RequestParam(name = "id", required = true) String id) {
		Result<JeecgDemo> result = new Result<JeecgDemo>();
		JeecgDemo jeecgDemo = jeecgDemoService.getById(id);
		if (jeecgDemo == null) {
			result.error500("未找到对应实体");
		} else {
			boolean ok = jeecgDemoService.removeById(id);
			if (ok) {
				result.success("删除成功!");
			}
		}

		return result;
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<JeecgDemo> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<JeecgDemo> result = new Result<JeecgDemo>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.jeecgDemoService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	 * 通过id查询
	 * 
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "获取Demo信息", tags = { "获取Demo信息" }, notes = "注意问题点")
	@GetMapping(value = "/queryById")
	public Result<JeecgDemo> queryById(@ApiParam(name = "id", value = "示例id", required = true) @RequestParam(name = "id", required = true) String id) {
		Result<JeecgDemo> result = new Result<JeecgDemo>();
		JeecgDemo jeecgDemo = jeecgDemoService.getById(id);
		if (jeecgDemo == null) {
			result.error500("未找到对应实体");
		} else {
			result.setResult(jeecgDemo);
			result.setSuccess(true);
		}
		return result;
	}
	

	// ================================================================================================================
	/**
	 * redis操作 -- set
	 */
	@GetMapping(value = "/redisSet")
	public void redisSet() {
		redisUtil.set("name", "张三" + DateUtils.now());
	}

	/**
	 * redis操作 -- get
	 */
	@GetMapping(value = "/redisGet")
	public String redisGet() {
		return (String) redisUtil.get("name");
	}

	/**
	 * redis操作 -- setObj
	 */
	@GetMapping(value = "/redisSetObj")
	public void redisSetObj() {
		JeecgDemo p = new JeecgDemo();
		p.setAge(10);
		p.setBirthday(new Date());
		p.setContent("hello");
		p.setName("张三");
		p.setSex("男");
		redisUtil.set("user-zdh", p);
	}

	/**
	 * redis操作 -- setObj
	 */
	@GetMapping(value = "/redisGetObj")
	public Object redisGetObj() {
		return redisUtil.get("user-zdh");
	}

	/**
	 * redis操作 -- get
	 */
	@GetMapping(value = "/redisDemo/{id}")
	public JeecgDemo redisGetJeecgDemo(@PathVariable("id") String id) {
		JeecgDemo t = jeecgDemoService.getByIdCacheable(id);
		System.out.println(t);
		return t;
	}

	/**
	 * freemaker方式 【页面路径： src/main/resources/templates】
	 * 
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping("/demo3")
	public ModelAndView demo3(ModelAndView modelAndView) {
		modelAndView.setViewName("demo3");
		List<String> userList = new ArrayList<String>();
		userList.add("admin");
		userList.add("user1");
		userList.add("user2");
		log.info("--------------test--------------");
		modelAndView.addObject("userList", userList);
		return modelAndView;
	}

	// ================================================================================================================

	/**
	 * hello world
	 * 测试helloWorld
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/hello")
	public Result<String> hello() {
        Result<String> result = new Result<String>();
        result.setResult("近几年来，父亲和我都是东奔西走，家中光景是一日不如一日。他少年出外谋生，独力支持，做了许多大事。哪知老境却如此颓唐！他触目伤怀，自然情不能自已。情郁于中，自然要发之于外；家庭琐屑便往往触他之怒。他待我渐渐不同往日。但最近两年不见，他终于忘却我的不好，只是惦记着我，惦记着他的儿子。我北来后，他写了一信给我，信中说道：“我身体平安，惟膀子疼痛厉害，举箸14提笔，诸多不便，大约大去之期15不远矣。”我读到此处，在晶莹的泪光中，又看见那肥胖的、青布棉袍黑布马褂的背影。唉！我不知何时再能与他相见！");
        result.setSuccess(true);
        return result;
    }
}

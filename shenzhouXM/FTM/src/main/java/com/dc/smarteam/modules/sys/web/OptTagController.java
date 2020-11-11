package com.dc.smarteam.modules.sys.web;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.utils.CacheUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.modules.sys.entity.Dict;
import com.dc.smarteam.modules.sys.entity.OptTag;
import com.dc.smarteam.modules.sys.service.DictService;
import com.dc.smarteam.modules.sys.service.OptTagService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by xuchuang on 2018/6/26.
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/tagsetting")
public class OptTagController extends BaseController {

    @Value("tag_type")
    private String dictType;
    @Resource
    private OptTagService optTagService;
    @Resource
    private DictService dictService;

    @RequestMapping(value = {"list",""})
    public String index(HttpServletRequest request, HttpServletResponse response, OptTag optTag, Model model){
        Dict dict = new Dict();
        dict.setType(dictType);
        List<Dict> dicts = dictService.findList(dict);
        logger.info("size:{}",dicts.size());
        model.addAttribute("dicts",dicts);
        Page<OptTag> optTagPage = optTagService.findPage(new Page<OptTag>(request,response),optTag);
        model.addAttribute("page",optTagPage);
        return "modules/sys/globalTag";
    }

    @RequestMapping(value = "setGlobalTag")
    public String setGlobalTag(Model model){
        Dict dict = new Dict();
        dict.setType(dictType);
        List<Dict> dicts = dictService.findList(dict);
        logger.info("size:{}",dicts.size());
        model.addAttribute("dicts",dicts);
        model.addAttribute("globalTag",(OptTag)CacheUtils.get(dictType));
        return "modules/sys/globalSet";
    }

    @RequestMapping(value = "getTags")
    @ResponseBody
    public Map<String,List<OptTag>> getTags(OptTag optTag){
        logger.info("optTag:{}",optTag.toString());
        List<OptTag> optTags = optTagService.findList(optTag);
        for(OptTag opt:optTags){
            logger.info("opt:{},{}",opt.getId(),opt.getName());
        }
        Map<String,List<OptTag>> data = new HashMap<>();
        data.put("data",optTags);
        return data;
    }

    @RequestMapping(value = "setGlobal")
    @ResponseBody
    public Map<String,String> setGlobal(OptTag optTag){
        logger.info("设置全局标签:{}",optTag.getId());
        optTag = optTagService.get(optTag);
        Map<String,String> map = new HashMap<>();
        if(optTag!=null){
            CacheUtils.put(dictType,optTag);
            map.put("flag",Boolean.toString(true));
            map.put("val",optTag.getName());
        }else{
            map.put("flag",Boolean.toString(false));
        }
        return map;
    }

    @RequestMapping(value = "addTag")
    public String addTag(Model model,OptTag optTag){
        Dict dict = new Dict();
        dict.setType(dictType);
        List<Dict> dicts = dictService.findList(dict);
        model.addAttribute("dicts",dicts);
        model.addAttribute("optTag",optTag);
        return "modules/sys/globalTagForm";
    }

    @RequestMapping(value = "add")
    public String add(OptTag optTag, HttpServletRequest request, RedirectAttributes redirectAttributes){
        String path=request.getServletContext().getContextPath();
        //optTag设置样式
        String[] colors = {"#FFC0CB","#E6E6FA","#6495ED","#7FFFD4","#ADFF2F","#F0E68C","#FFA07A","#C0C0C0"};
        int i = new Random().nextInt(8);//NOSONAR
        StringBuffer sb = new StringBuffer();
        sb.append("&lt;td id=&quot;draggable1&quot; ")
                .append("class=&quot;ui-draggable ui-draggable-handle&quot; width=&quot;90&quot; bgcolor=&quot;" )
                .append(colors[i])
                .append("&quot;&gt;")
                .append(optTag.getName())
                .append("&lt;img src=&quot;")
                .append(path)
                .append("/static/jQuery-ui/btn_delete.png&quot; ")
                .append("style=&quot;display:none&quot;&gt;&lt;/td&gt;");
        optTag.setStyle(sb.toString());
        int addRes = optTagService.add(optTag);
        if(addRes>0){
            addMessage(redirectAttributes,"标签添加成功");
        }else if(addRes==-1){
            addMessage(redirectAttributes,"标签添加失败:[重复的标签]");
        }else{
            addMessage(redirectAttributes,"标签添加失败:[发生异常]");
        }
        return "redirect:list";
    }

    @ResponseBody
    @RequestMapping(value = "del")
    public String del(OptTag optTag){
        optTag.setStatus(0);
        if(optTagService.update(optTag)>0){
            return Boolean.toString(true);
        }
        return Boolean.toString(false);
    }

}

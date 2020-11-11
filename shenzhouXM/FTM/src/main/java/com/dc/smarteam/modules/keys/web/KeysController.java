package com.dc.smarteam.modules.keys.web;

import com.dc.smarteam.cfgmodel.CfgModelConverter;
import com.dc.smarteam.cfgmodel.KeysModel;
import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.PageHelper;
import com.dc.smarteam.modules.keys.entity.FtKey;
import com.dc.smarteam.modules.sys.entity.Dict;
import com.dc.smarteam.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "${adminPath}/keys/ftKey")
public class KeysController extends BaseController {
    @Autowired
    private KeyService keyService;
    @RequestMapping(value = {"list", ""})
    public String list(FtKey ftKey,HttpServletRequest request, Model model,HttpServletResponse response, RedirectAttributes redirectAttributes) {// NOSONAR

        List<FtKey> list = new ArrayList<>();
        ResultDto<List<KeysModel.Key>> dto = keyService.listAll();
        if (ResultDtoTool.isSuccess(dto)) {
            List<KeysModel.Key> keys = dto.getData();
            String ftKeyUser = ftKey.getUser();
            for (KeysModel.Key key : keys) {
                if (StringUtils.isNoneEmpty(ftKeyUser) && !StringUtils.containsIgnoreCase(key.getUser(), ftKeyUser))
                    continue;
                FtKey newFtKey = new FtKey();
                CfgModelConverter.convertTo(newFtKey,key);
                newFtKey.setId(String.valueOf(list.size()));
                list.add(newFtKey);
            }
        } else {
            addMessage(redirectAttributes, dto.getMessage());
            return "redirect:" + Global.getAdminPath() + "/servicenode/ftServiceNode/nodeList";
        }
        PageHelper.getInstance().getPage(ftKey.getClass(), request, response, model, list);
        return "modules/keys/keysForm";
    }

    @RequestMapping(value = "addPage")
    public String addPage(FtKey ftKey, HttpServletRequest request, Model model) {
        System.out.println("添加页面");
        List<Dict> dictList = new ArrayList<>();
        for (int i= 0;i<3;i++){
            Dict dict = new Dict();
            if (i==0){
                dict.setValue("p");
            } else if (i==1){
                dict.setValue("s");
            }
            dictList.add(dict);
        }
        model.addAttribute("dictList", dictList);
            return "modules/keys/keysAddForm";
    }

    @RequestMapping(value = "save")
    public String save(FtKey ftKey, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        ResultDto<String> resultDto = keyService.add(ftKey);
        if (ResultDtoTool.isSuccess(resultDto) ) {
            addMessage(redirectAttributes, "秘钥保存成功");
        } else {
            addMessage(redirectAttributes, resultDto.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/keys/ftKey/?repage";//NOSONAR
    }

    @RequestMapping(value = "delete")
    public String delete(FtKey ftKey, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ResultDto<String> dto = keyService.del(ftKey);
        if (!ResultDtoTool.isSuccess(dto)) {
            String message = dto.getMessage();
            addMessage(redirectAttributes, message);
        }
        return "redirect:" + Global.getAdminPath() + "/keys/ftKey/?repage";
    }


    @RequestMapping(value = "form")//修改
    public String form(FtKey ftKey, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        if (StringUtils.isEmpty(ftKey.getUser())) {
            return "modules/keys/keysEditForm";
        }
        ResultDto<KeysModel.Key> dto = keyService.selByUser(ftKey);
        FtKey newFtKey = new FtKey();
        if (ResultDtoTool.isSuccess(dto)) {
            KeysModel.Key key = dto.getData();
            CfgModelConverter.convertTo(newFtKey,key);
        } else {
            addMessage(redirectAttributes, dto.getMessage());
            return "redirect:" + Global.getAdminPath() + "/keys/ftKey/?repage";
        }
        model.addAttribute("ftKey", newFtKey);
        return "modules/keys/keysEditForm";
    }

    @RequestMapping(value = "saveEdit")//修改保存
    public String saveEdit(FtKey ftKey, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ResultDto<String> dto = keyService.update(ftKey);
        if (!ResultDtoTool.isSuccess(dto)) {
            String message = dto.getMessage();
            addMessage(redirectAttributes, message);
        }
        return "redirect:" + Global.getAdminPath() + "/keys/ftKey/?repage";
    }
}

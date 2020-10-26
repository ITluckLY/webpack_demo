package com.dc.smarteam.helper;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.JsonToEntityFactory;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.msggenerator.FtUserMsgGen;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.user.entity.FtUser;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenhuae on 2017/1/5.
 */
public class FtUserHelper {

    private static FtUserHelper single = null;

    private FtUserHelper() {
    }

    public static FtUserHelper getInstance() {
        if (single == null) {
            single = new FtUserHelper();
        }
        return single;
    }

    public List getFtUserList(FtUser ftUser, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {//NOSONAR
        FtServiceNode ftServiceNode = CurrNameNodeHelper.getCurrNameNode(request);
        ftUser.setSystemName(ftServiceNode.getSystemName());
        String str = FtUserMsgGen.getAll();
        TCPAdapter tcpAdapter = new TCPAdapter();
        ResultDto<String> resultDto = tcpAdapter.invoke(str, ftServiceNode, String.class);//发送报文
        List<FtUser> list;
        List<FtUser> list2 = new ArrayList<FtUser>();
        List<FtUser> list3 = new ArrayList<FtUser>();
        List<FtUser> list4 = new ArrayList<FtUser>();
        if (ResultDtoTool.isSuccess(resultDto)) {
            String data = resultDto.getData();
            list = JsonToEntityFactory.getInstance().getFtUserList(data);
            for (FtUser fu : list) {
                StringBuilder sb = new StringBuilder(100);
                String userDirTemp = null;
                if (fu.getUserDir().startsWith("/")) {
                    userDirTemp = sb.append("/").append(ftServiceNode.getSystemName()).append(fu.getUserDir()).toString();
                } else {
                    userDirTemp = sb.append("/").append(ftServiceNode.getSystemName()).append("/").append(fu.getUserDir()).toString();
                }
                fu.setUserDir(userDirTemp);
                list2.add(fu);
            }
            boolean name = false;
            if (ftUser.getName() != null && !"".equals(ftUser.getName())) {
                name = true;
            }
            if (name) {
                for (int i = 0; i < list2.size(); i++) {
                    FtUser ft = list2.get(i);
                    if (!ft.getName().toLowerCase().contains(ftUser.getName().toLowerCase())) {
                        continue;
                    }
                    list3.add(list2.get(i));
                }
            } else {
                list3 = list2;
            }
            for (FtUser fu : list3) {
                if (null == fu.getDes() || fu.getDes().equalsIgnoreCase("null")) {
                    fu.setDes("");
                }
                list4.add(fu);
            }
        }
        return list4;
    }
}

/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.common.supcan.freeform;

import com.dc.smarteam.common.supcan.common.Common;
import com.dc.smarteam.common.supcan.common.properties.Properties;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 硕正FreeForm
 *
 * @author WangZhen
 * @version 2013-11-04
 */
@XStreamAlias("FreeForm")
public class FreeForm extends Common {

    public FreeForm() {
        super();
    }

    public FreeForm(Properties properties) {
        this();
        this.properties = properties;
    }

}

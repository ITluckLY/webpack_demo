package com.dc.smarteam.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.*;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;

/**
 * Created by zctang on 2014/8/29 0029.
 */
public class XmlBeanUtil {//NOSONAR
    private static final Logger log = LoggerFactory.getLogger(XmlBeanUtil.class);

    public static <T> String toXmlIgnoreClass(T entity) {
        if (entity == null) return null;
        XStream stream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")) {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new CompactWriter(out, getNameCoder());
            }
        });
        stream.aliasSystemAttribute(null, "class");
        stream.processAnnotations(entity.getClass());
        return stream.toXML(entity);
    }

    public static <T> String toXml(T entity) {
        if (entity == null) return null;
        XStream stream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")) {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new CompactWriter(out, getNameCoder());
            }
        });
        stream.processAnnotations(entity.getClass());
        return stream.toXML(entity);
    }

    public static <T> String toXmlIgnoreEmpty(T entity) {
        if (entity == null) return null;
        XStream stream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")) {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new CompactWriter(out, getNameCoder());
            }
        });
        processXmlIgnore(entity);
        stream.processAnnotations(entity.getClass());
        return stream.toXML(entity);
    }

    /**
     * 处理值为空字符串的节点和空数组节点，均设为空节点（不传该节点）
     *
     * @param entity 对象实例
     * @param <T>    对象实例类型
     */
    private static <T> void processXmlIgnore(T entity) {
        org.springframework.util.ReflectionUtils.getUniqueDeclaredMethods(entity.getClass());
    }

    public static <T> String toXmlContaintHead(T entity) {
        if (entity == null) return null;
        XStream stream = new XStream(new Dom4JDriver(new XmlFriendlyNameCoder("_-", "_")) {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new CompactWriter(out, getNameCoder());
            }
        });
        stream.processAnnotations(entity.getClass());
        return stream.toXML(entity);
    }

    @SuppressWarnings("unchecked")
    public static <T> T toEntity(String xml, Class<T> cls) {
        XStream xstream = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("_-", "_")) {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new CompactWriter(out, getNameCoder());
            }
        });
        xstream.processAnnotations(cls);
        return (T) xstream.fromXML(xml);
    }

    public static <T> String logToXml(T entity) {
        if (entity == null) return null;
        try {
            XStream stream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")) {
                @Override
                public HierarchicalStreamWriter createWriter(Writer out) {
                    return new CompactWriter(out, getNameCoder());
                }
            });
            stream.processAnnotations(entity.getClass());
            return stream.toXML(entity);
        } catch (Throwable e) {//NOSONAR
            log.error("", e);
            return "logToXmlErr";
        }
    }

    public static String convertJsonToXml(String json, String root) {
        JSON jsonObject = JSONSerializer.toJSON(json);
        net.sf.json.xml.XMLSerializer xmlSerializer = new net.sf.json.xml.XMLSerializer();
        xmlSerializer.setRootName(root);
        xmlSerializer.setObjectName(null);
        xmlSerializer.setSkipNamespaces(true);
        xmlSerializer.setTypeHintsEnabled(false);
        xmlSerializer.setRemoveNamespacePrefixFromElements(true);
        xmlSerializer.setForceTopLevelObject(true);
        String xml = xmlSerializer.write(jsonObject);
        return xml;
    }

    public static <T> T convertJsonToXmlEntity(String json, Class<T> cls) {
        T entity = EntityConveterUtils.conveterJsonToEntity(json, cls);
        return entity;
    }

    public static <T> String convertJsonToXml(String json, Class<T> cls) {
        T entity = EntityConveterUtils.conveterJsonToEntity(json, cls);
        return toXml(entity);
    }

    public static String convertXmlToJson(String xml) {
        String json = "";
        net.sf.json.xml.XMLSerializer xmlSerializer = new net.sf.json.xml.XMLSerializer();
        JSON jsonObject = xmlSerializer.read(xml);
        json = jsonObject.toString();
        return json;
    }
}

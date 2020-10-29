package com.simple.xrcraft.common.utils.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by lixiaorong on 2017/8/29.
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        //allow null to object
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        //java 8 time stuff
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        mapper.registerModule(javaTimeModule);
    }

    /**
     * bean to json
     * @param bean
     * @return
     */
    public static String toJson(Object bean) {
        return toJson(bean, null);
    }

    /**
     * bean to json, ignore some fields
     * @param bean
     * @param ignoreVar
     * @return
     */
    public static String toJson(Object bean, String ... ignoreVar) {

        if(null == bean){
            return null;
        }
        StringWriter sw = new StringWriter();
        try {
            JsonNode rootNode = null;
            boolean haveIgnoredVar = null != ignoreVar && ignoreVar.length > 0;
            if(haveIgnoredVar){
                rootNode = mapper.valueToTree(bean);
                remove(rootNode, ignoreVar);
            }

            JsonGenerator gen = new JsonFactory().createGenerator(sw);
            mapper.writeValue(gen, haveIgnoredVar ? rootNode : bean);
        } catch (Exception e) {
            log.error("转换异常");
            return null;
        }
        return sw.toString();
    }

    /**
     * cascade remove node from json string
     * @param node
     * @param ignoreVar
     */
    public static void remove(JsonNode node, String... ignoreVar) {

        if(ignoreVar == null || ignoreVar.length == 0
                || null == node) {
            return;
        }

        for (String var : ignoreVar) {
            ((ObjectNode) node).remove(var);
        }

        Iterator<JsonNode> it = node.elements();
        while(it.hasNext()){
            JsonNode childNode = it.next();
            if(childNode.isContainerNode()) {
                remove(childNode, ignoreVar);
            }
        }
    }

    /**
     * json to Map
     * @param jsonStr
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static Map<String, Object> json2Map(String jsonStr) throws IOException {
        return jsonToObject(jsonStr, Map.class);
    }

    /**
     * json to bean
      * @param jsonStr
     * @param beanClass
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T jsonToObject(String jsonStr, Class<T> beanClass) throws IOException {
        return jsonToObject(jsonStr, null, beanClass);
    }

    /**
     * json to bean
     * @param jsonStr
     * @param charset
     * @param beanClass
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T jsonToObject(String jsonStr, String charset, Class<T> beanClass)
            throws IOException {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        return mapper.readValue(StringUtils.isBlank(charset) ? jsonStr.getBytes() : jsonStr.getBytes(charset), beanClass);
    }

    /**
     * json to collection
     * @param jsonStr
     * @param cClass
     * @param eClasses
     * @return
     * @throws IOException
     */
    public static <C, E> C jsonToCollection(String jsonStr, Class<C> cClass,
            Class<E> eClasses) throws IOException {
       return jsonToCollection(jsonStr, null, cClass, eClasses);
    }

    /**
     * json to collection
     * @param jsonStr
     * @param charset
     * @param cClass
     * @param eClasses
     * @return
     * @throws IOException
     */
    public static <C, E> C jsonToCollection(String jsonStr, String charset,
            Class<C> cClass,
            Class<E> eClasses) throws IOException {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        JavaType javaType = getParametrizedType(cClass, eClasses);
        return mapper.readValue(StringUtils.isBlank(charset) ? jsonStr.getBytes() : jsonStr.getBytes(charset), javaType);
    }

    /**
     * assemble JavaType
     * @param parametrized
     * @param parameterClasses
     * @return
     */
    public static JavaType getParametrizedType(Class<?> parametrized, Class<?>... parameterClasses) {
        return mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

}

	
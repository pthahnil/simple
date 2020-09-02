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

        //空字符串转object
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        //java 8 日期时间格式
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        mapper.registerModule(javaTimeModule);
    }


    /**
     * bean to json
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        StringWriter sw = new StringWriter();
        try {
            JsonGenerator gen = new JsonFactory().createGenerator(sw);
            mapper.writeValue(gen, obj);
            gen.close();
            return sw.toString();
        } catch (IOException e) {
            log.error("obj to string error ");
            return null;
        }
    }

    /**
     * bean to json, ignore some fields
     * @param bean
     * @param ignoreVar
     * @return
     */
    public static String toJson(Object bean, String... ignoreVar) {

        if(null == bean){
            return null;
        }
        JsonNode rootNode = null;
        StringWriter sw = new StringWriter();
        try {
            rootNode = mapper.valueToTree(bean);

            remove(rootNode, ignoreVar);

            JsonGenerator jsonGenerator = new JsonFactory().createGenerator(sw);
            mapper.writeValue(jsonGenerator,rootNode);
        } catch (Exception e) {
            log.error("转换异常");
            return null;
        }
        return sw.toString();
    }

    /**
     * 级联删除node
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
        if (StringUtils.isBlank(jsonStr)) {
            return Collections.emptyMap();
        }
        return mapper.readValue(jsonStr, Map.class);
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
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        return mapper.readValue(jsonStr, beanClass);
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
        return mapper.readValue(jsonStr.getBytes(charset), beanClass);
    }

    /**
     * json to collection
     * @param jsonStr
     * @param collectionClass
     * @param elementClasses
     * @return
     * @throws IOException
     */
    public static Object jsonToCollection(String jsonStr, Class<?> collectionClass,
            Class<?>... elementClasses) throws IOException {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        JavaType javaType = getParametrizedType(collectionClass, elementClasses);
        return mapper.readValue(jsonStr, javaType);
    }

    /**
     * json to collection
     * @param jsonStr
     * @param encoding
     * @param collectionClass
     * @param elementClasses
     * @return
     * @throws IOException
     */
    public static Object jsonToCollection(String jsonStr, String encoding,
            Class<?> collectionClass, Class<?>... elementClasses) throws IOException {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        JavaType javaType = getParametrizedType(collectionClass, elementClasses);
        return mapper.readValue(jsonStr.getBytes(encoding), javaType);
    }

    /**
     * 获取集合的JavaType
     * @param parametrized
     * @param parameterClasses
     * @return
     */
    public static JavaType getParametrizedType(Class<?> parametrized, Class<?>... parameterClasses) {
        return mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }


    public static String normalize(String jsonStr) {
        if(StringUtils.isBlank(jsonStr)) {
            return null;
        }
        return jsonStr.replace("\"{", "{").replace("}\"", "}")
                .replace("\"[", "[").replace("]\"", "]")
                .replaceAll("\\\\","");
    }

}

	
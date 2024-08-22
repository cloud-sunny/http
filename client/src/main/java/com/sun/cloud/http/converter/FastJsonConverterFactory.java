package com.sun.cloud.http.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;

/**
 * Created on 2018/8/22 20:32
 *
 * @author WingHawk
 */
public class FastJsonConverterFactory extends Factory {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    private ParserConfig parserConfig = ParserConfig.getGlobalInstance();
    private int featureValues = JSON.DEFAULT_PARSER_FEATURE;
    private Feature[] features;

    private SerializeConfig serializeConfig;
    private SerializerFeature[] serializerFeatures;

    public FastJsonConverterFactory() {
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, //
                                                            Annotation[] annotations, //
                                                            Retrofit retrofit) {
        return new ResponseBodyConverter<ResponseBody>(type, parserConfig, featureValues, features);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, //
                                                          Annotation[] parameterAnnotations, //
                                                          Annotation[] methodAnnotations, //
                                                          Retrofit retrofit) {
        return new RequestBodyConverter<RequestBody>();
    }

    public ParserConfig getParserConfig() {
        return parserConfig;
    }

    public FastJsonConverterFactory setParserConfig(ParserConfig config) {
        this.parserConfig = config;
        return this;
    }

    public int getParserFeatureValues() {
        return featureValues;
    }

    public FastJsonConverterFactory setParserFeatureValues(int featureValues) {
        this.featureValues = featureValues;
        return this;
    }

    public Feature[] getParserFeatures() {
        return features;
    }

    public FastJsonConverterFactory setParserFeatures(Feature[] features) {
        this.features = features;
        return this;
    }

    public SerializeConfig getSerializeConfig() {
        return serializeConfig;
    }

    public FastJsonConverterFactory setSerializeConfig(SerializeConfig serializeConfig) {
        this.serializeConfig = serializeConfig;
        return this;
    }

    public SerializerFeature[] getSerializerFeatures() {
        return serializerFeatures;
    }

    public FastJsonConverterFactory setSerializerFeatures(SerializerFeature[] features) {
        this.serializerFeatures = features;
        return this;
    }

    final class RequestBodyConverter<T> implements Converter<T, RequestBody> {
        RequestBodyConverter() {
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            byte[] content = JSON.toJSONBytes(value
                    , serializeConfig == null
                            ? SerializeConfig.globalInstance
                            : serializeConfig
                    , serializerFeatures == null
                            ? SerializerFeature.EMPTY
                            : serializerFeatures
            );

            return RequestBody.create(MEDIA_TYPE, content);
        }
    }
}

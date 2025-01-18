//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.http.codec.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.micrometer.core.instrument.Metrics;
import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.codec.HttpMessageDecoder;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

public abstract class AbstractJackson2Decoder extends Jackson2CodecSupport implements HttpMessageDecoder<Object> {
    private int maxInMemorySize = 262144;

    protected AbstractJackson2Decoder(ObjectMapper mapper, MimeType... mimeTypes) {
        super(mapper, mimeTypes);
    }

    public void setMaxInMemorySize(int byteCount) {
        this.maxInMemorySize = byteCount;
    }

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        // TODO: tutaj wpada ten wątek XX, w sensie jakiś ten, co ogarnia faktyczną robotę
        if (!this.supportsMimeType(mimeType)) {
            return false;
        } else {
            ObjectMapper mapper = this.selectObjectMapper(elementType, mimeType);
            if (mapper == null) {
                return false;
            } else if (CharSequence.class.isAssignableFrom(elementType.toClass())) {
                return false;
            } else {
                JavaType javaType = mapper.constructType(elementType.getType());
                if (!this.logger.isDebugEnabled()) {
                    return mapper.canDeserialize(javaType);
                } else {
                    AtomicReference<Throwable> causeRef = new AtomicReference();
                    if (mapper.canDeserialize(javaType, causeRef)) {
                        return true;
                    } else {
                        this.logWarningIfNecessary(javaType, (Throwable)causeRef.get());
                        return false;
                    }
                }
            }
        }
    }

    public Flux<Object> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        ObjectMapper mapper = this.selectObjectMapper(elementType, mimeType);
        if (mapper == null) {
            return Flux.error(new IllegalStateException("No ObjectMapper for " + elementType));
        } else {
            boolean forceUseOfBigDecimal = mapper.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            if (BigDecimal.class.equals(elementType.getType())) {
                forceUseOfBigDecimal = true;
            }

            Flux<DataBuffer> processed = this.processInput(input, elementType, mimeType, hints);
            Flux<TokenBuffer> tokens = Jackson2Tokenizer.tokenize(processed, mapper.getFactory(), mapper, true, forceUseOfBigDecimal, this.getMaxInMemorySize());
            return Flux.deferContextual((contextView) -> {
                Map<String, Object> hintsToUse = contextView.isEmpty() ? hints : Hints.merge(hints, ContextView.class.getName(), contextView);
                ObjectReader reader = this.createObjectReader(mapper, elementType, hintsToUse);
                return tokens.handle((tokenBuffer, sink) -> {
                    try {
                        Object value = reader.readValue(tokenBuffer.asParser(mapper));
                        this.logValue(value, hints);
                        if (value != null) {
                            sink.next(value);
                        }
                    } catch (IOException var7) {
                        IOException ex = var7;
                        sink.error(this.processException(ex));
                    }

                });
            });
        }
    }

    protected Flux<DataBuffer> processInput(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(input);
    }

    public Mono<Object> decodeToMono(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Mono.deferContextual((contextView) -> {
            // tu juz zaczac czas????
            Map<String, Object> hintsToUse = contextView.isEmpty() ? hints : Hints.merge(hints, ContextView.class.getName(), contextView);
            return DataBufferUtils
                    .join(input, this.maxInMemorySize)
//                    .publishOn(paraXD)
                    .flatMap(dataBuffer ->
                            Mono.justOrEmpty(decode(dataBuffer, elementType, mimeType, hintsToUse))
                    );
        });
    }
//    private static final Scheduler paraXD = Schedulers.newParallel("XD");


    public Object decode(DataBuffer dataBuffer, ResolvableType targetType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) throws DecodingException {
        ObjectMapper mapper = this.selectObjectMapper(targetType, mimeType);
        if (mapper == null) {
            throw new IllegalStateException("No ObjectMapper for " + targetType);
        } else {
            Object var8;
            Long startXDDD = System.nanoTime();
            try {



                ObjectReader objectReader = this.createObjectReader(mapper, targetType, hints);
                Object value = objectReader.readValue(dataBuffer.asInputStream());
                this.logValue(value, hints);
                var8 = value;
            } catch (IOException var12) {
                IOException ex = var12;
                throw this.processException(ex);
            } finally {
                DataBufferUtils.release(dataBuffer);

                Logeusz.INSTANCE.loguj("decoder jest tutaj");
                Long endDummyValue = System.nanoTime();
                Duration dummyValueDuration = Duration.ofNanos(endDummyValue - startXDDD);
                Metrics.timer("decodeValue").record(dummyValueDuration);
            }

            return var8;
        }
    }

    private ObjectReader createObjectReader(ObjectMapper mapper, ResolvableType elementType, @Nullable Map<String, Object> hints) {
        Assert.notNull(elementType, "'elementType' must not be null");
        Class<?> contextClass = this.getContextClass(elementType);
        if (contextClass == null && hints != null) {
            contextClass = this.getContextClass((ResolvableType)hints.get(ACTUAL_TYPE_HINT));
        }

        JavaType javaType = this.getJavaType(elementType.getType(), contextClass);
        Class<?> jsonView = hints != null ? (Class)hints.get(Jackson2CodecSupport.JSON_VIEW_HINT) : null;
        ObjectReader objectReader = jsonView != null ? mapper.readerWithView(jsonView).forType(javaType) : mapper.readerFor(javaType);
        return this.customizeReader(objectReader, elementType, hints);
    }

    protected ObjectReader customizeReader(ObjectReader reader, ResolvableType elementType, @Nullable Map<String, Object> hints) {
        return reader;
    }

    @Nullable
    private Class<?> getContextClass(@Nullable ResolvableType elementType) {
        MethodParameter param = elementType != null ? this.getParameter(elementType) : null;
        return param != null ? param.getContainingClass() : null;
    }

    private void logValue(@Nullable Object value, @Nullable Map<String, Object> hints) {
        if (!Hints.isLoggingSuppressed(hints)) {
            LogFormatUtils.traceDebug(this.logger, (traceOn) -> {
                String formatted = LogFormatUtils.formatValue(value, !traceOn);
                String var10000 = Hints.getLogPrefix(hints);
                return var10000 + "Decoded [" + formatted + "]";
            });
        }

    }

    private CodecException processException(IOException ex) {
        if (ex instanceof InvalidDefinitionException ide) {
            JavaType type = ide.getType();
            return new CodecException("Type definition error: " + type, ex);
        } else if (ex instanceof JsonProcessingException jpe) {
            String originalMessage = jpe.getOriginalMessage();
            return new DecodingException("JSON decoding error: " + originalMessage, ex);
        } else {
            return new DecodingException("I/O error while parsing input stream", ex);
        }
    }

    public Map<String, Object> getDecodeHints(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request, ServerHttpResponse response) {
        return this.getHints(actualType);
    }

    public List<MimeType> getDecodableMimeTypes() {
        return this.getMimeTypes();
    }

    public List<MimeType> getDecodableMimeTypes(ResolvableType targetType) {
        return this.getMimeTypes(targetType);
    }

    @Nullable
    protected <A extends Annotation> A getAnnotation(MethodParameter parameter, Class<A> annotType) {
        return parameter.getParameterAnnotation(annotType);
    }
}

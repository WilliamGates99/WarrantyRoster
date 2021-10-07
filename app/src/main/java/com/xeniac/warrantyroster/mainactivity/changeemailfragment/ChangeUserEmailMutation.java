package com.xeniac.warrantyroster.mainactivity.changeemailfragment;// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//

import com.apollographql.apollo.api.Mutation;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.api.ScalarTypeAdapters;
import com.apollographql.apollo.api.internal.InputFieldMarshaller;
import com.apollographql.apollo.api.internal.InputFieldWriter;
import com.apollographql.apollo.api.internal.OperationRequestBodyComposer;
import com.apollographql.apollo.api.internal.QueryDocumentMinifier;
import com.apollographql.apollo.api.internal.ResponseFieldMapper;
import com.apollographql.apollo.api.internal.ResponseFieldMarshaller;
import com.apollographql.apollo.api.internal.ResponseReader;
import com.apollographql.apollo.api.internal.ResponseWriter;
import com.apollographql.apollo.api.internal.SimpleOperationResponseParser;
import com.apollographql.apollo.api.internal.UnmodifiableMapBuilder;
import com.apollographql.apollo.api.internal.Utils;

import java.io.IOException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;

import org.jetbrains.annotations.NotNull;

public final class ChangeUserEmailMutation implements Mutation<ChangeUserEmailMutation.Data, ChangeUserEmailMutation.Data, ChangeUserEmailMutation.Variables> {
    public static final String OPERATION_ID = "3d285628b9906c16b8f47724fe81ce9f1a35f0f019319226bf04e955ace7f8ee";

    public static final String QUERY_DOCUMENT = QueryDocumentMinifier.minify(
            "mutation changeUserEmail($email: String!) {\n"
                    + "  changeUserEmail(email: $email)\n"
                    + "}"
    );

    public static final OperationName OPERATION_NAME = new OperationName() {
        @Override
        public String name() {
            return "changeUserEmail";
        }
    };

    private final ChangeUserEmailMutation.Variables variables;

    public ChangeUserEmailMutation(@NotNull String email) {
        Utils.checkNotNull(email, "email == null");
        variables = new ChangeUserEmailMutation.Variables(email);
    }

    @Override
    public String operationId() {
        return OPERATION_ID;
    }

    @Override
    public String queryDocument() {
        return QUERY_DOCUMENT;
    }

    @Override
    public ChangeUserEmailMutation.Data wrapData(ChangeUserEmailMutation.Data data) {
        return data;
    }

    @Override
    public ChangeUserEmailMutation.Variables variables() {
        return variables;
    }

    @Override
    public ResponseFieldMapper<ChangeUserEmailMutation.Data> responseFieldMapper() {
        return new Data.Mapper();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public OperationName name() {
        return OPERATION_NAME;
    }

    @Override
    @NotNull
    public Response<ChangeUserEmailMutation.Data> parse(@NotNull final BufferedSource source,
                                                        @NotNull final ScalarTypeAdapters scalarTypeAdapters) throws IOException {
        return SimpleOperationResponseParser.parse(source, this, scalarTypeAdapters);
    }

    @Override
    @NotNull
    public Response<ChangeUserEmailMutation.Data> parse(@NotNull final ByteString byteString,
                                                        @NotNull final ScalarTypeAdapters scalarTypeAdapters) throws IOException {
        return parse(new Buffer().write(byteString), scalarTypeAdapters);
    }

    @Override
    @NotNull
    public Response<ChangeUserEmailMutation.Data> parse(@NotNull final BufferedSource source) throws
            IOException {
        return parse(source, ScalarTypeAdapters.DEFAULT);
    }

    @Override
    @NotNull
    public Response<ChangeUserEmailMutation.Data> parse(@NotNull final ByteString byteString) throws
            IOException {
        return parse(byteString, ScalarTypeAdapters.DEFAULT);
    }

    @Override
    @NotNull
    public ByteString composeRequestBody(@NotNull final ScalarTypeAdapters scalarTypeAdapters) {
        return OperationRequestBodyComposer.compose(this, false, true, scalarTypeAdapters);
    }

    @NotNull
    @Override
    public ByteString composeRequestBody() {
        return OperationRequestBodyComposer.compose(this, false, true, ScalarTypeAdapters.DEFAULT);
    }

    @Override
    @NotNull
    public ByteString composeRequestBody(final boolean autoPersistQueries,
                                         final boolean withQueryDocument, @NotNull final ScalarTypeAdapters scalarTypeAdapters) {
        return OperationRequestBodyComposer.compose(this, autoPersistQueries, withQueryDocument, scalarTypeAdapters);
    }

    public static final class Builder {
        private @NotNull
        String email;

        Builder() {
        }

        public Builder email(@NotNull String email) {
            this.email = email;
            return this;
        }

        public ChangeUserEmailMutation build() {
            Utils.checkNotNull(email, "email == null");
            return new ChangeUserEmailMutation(email);
        }
    }

    public static final class Variables extends Operation.Variables {
        private final @NotNull
        String email;

        private final transient Map<String, Object> valueMap = new LinkedHashMap<>();

        Variables(@NotNull String email) {
            this.email = email;
            this.valueMap.put("email", email);
        }

        public @NotNull
        String email() {
            return email;
        }

        @Override
        public Map<String, Object> valueMap() {
            return Collections.unmodifiableMap(valueMap);
        }

        @Override
        public InputFieldMarshaller marshaller() {
            return new InputFieldMarshaller() {
                @Override
                public void marshal(InputFieldWriter writer) throws IOException {
                    writer.writeString("email", email);
                }
            };
        }
    }

    /**
     * Data from the response after executing this GraphQL operation
     */
    public static class Data implements Operation.Data {
        static final ResponseField[] $responseFields = {
                ResponseField.forString("changeUserEmail", "changeUserEmail", new UnmodifiableMapBuilder<String, Object>(1)
                        .put("email", new UnmodifiableMapBuilder<String, Object>(2)
                                .put("kind", "Variable")
                                .put("variableName", "email")
                                .build())
                        .build(), false, Collections.<ResponseField.Condition>emptyList())
        };

        final @NotNull
        String changeUserEmail;

        private transient volatile String $toString;

        private transient volatile int $hashCode;

        private transient volatile boolean $hashCodeMemoized;

        public Data(@NotNull String changeUserEmail) {
            this.changeUserEmail = Utils.checkNotNull(changeUserEmail, "changeUserEmail == null");
        }

        public @NotNull
        String changeUserEmail() {
            return this.changeUserEmail;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public ResponseFieldMarshaller marshaller() {
            return new ResponseFieldMarshaller() {
                @Override
                public void marshal(ResponseWriter writer) {
                    writer.writeString($responseFields[0], changeUserEmail);
                }
            };
        }

        @Override
        public String toString() {
            if ($toString == null) {
                $toString = "Data{"
                        + "changeUserEmail=" + changeUserEmail
                        + "}";
            }
            return $toString;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Data) {
                Data that = (Data) o;
                return this.changeUserEmail.equals(that.changeUserEmail);
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (!$hashCodeMemoized) {
                int h = 1;
                h *= 1000003;
                h ^= changeUserEmail.hashCode();
                $hashCode = h;
                $hashCodeMemoized = true;
            }
            return $hashCode;
        }

        public static final class Mapper implements ResponseFieldMapper<Data> {
            @Override
            public Data map(ResponseReader reader) {
                final String changeUserEmail = reader.readString($responseFields[0]);
                return new Data(changeUserEmail);
            }
        }
    }
}
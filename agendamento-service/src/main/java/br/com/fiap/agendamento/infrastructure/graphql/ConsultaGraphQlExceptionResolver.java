package br.com.fiap.agendamento.infrastructure.graphql;

import br.com.fiap.agendamento.domain.exception.AccessDeniedBusinessException;
import br.com.fiap.agendamento.domain.exception.BusinessException;
import br.com.fiap.agendamento.domain.exception.ResourceNotFoundException;
import graphql.GraphQLError;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Traduz as exceções de domínio lançadas pelos resolvers GraphQL em erros
 * GraphQL com a classificação correta, evitando que sejam mascaradas como
 * INTERNAL_ERROR (genérico, equivalente a um HTTP 500).
 *
 * <ul>
 *     <li>{@link ResourceNotFoundException} -&gt; NOT_FOUND</li>
 *     <li>{@link AccessDeniedBusinessException} -&gt; FORBIDDEN</li>
 *     <li>{@link BusinessException} -&gt; BAD_REQUEST</li>
 * </ul>
 */
@ControllerAdvice
public class ConsultaGraphQlExceptionResolver {

    @GraphQlExceptionHandler(ResourceNotFoundException.class)
    public GraphQLError handleResourceNotFound(ResourceNotFoundException ex) {
        return GraphQLError.newError()
                .errorType(ErrorType.NOT_FOUND)
                .message(ex.getMessage())
                .build();
    }

    @GraphQlExceptionHandler(AccessDeniedBusinessException.class)
    public GraphQLError handleAccessDenied(AccessDeniedBusinessException ex) {
        return GraphQLError.newError()
                .errorType(ErrorType.FORBIDDEN)
                .message(ex.getMessage())
                .build();
    }

    @GraphQlExceptionHandler(BusinessException.class)
    public GraphQLError handleBusinessException(BusinessException ex) {
        return GraphQLError.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
    }
}

package com.example.EcoGet.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 – Regra de negócio violada
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, Object>> handleRegraNegocio(RegraNegocioException ex) {
        return erro(HttpStatus.BAD_REQUEST, "Dados inválidos", ex.getMessage());
    }

    // 400 – Senha inválida
    @ExceptionHandler(SenhaInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleSenhaInvalida(SenhaInvalidaException ex) {
        return erro(HttpStatus.BAD_REQUEST, "Credenciais inválidas", "E-mail ou senha incorretos.");
    }

    // 400 – JSON malformado ou campos com tipo errado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMensagemIlegivel(HttpMessageNotReadableException ex) {
        return erro(HttpStatus.BAD_REQUEST, "Parâmetros inválidos",
                "O corpo da requisição está mal formatado ou contém campos com tipo incorreto.");
    }

    // 400 – Parâmetro de URL com tipo errado (ex: /api/v1/transacoes/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTipoErrado(MethodArgumentTypeMismatchException ex) {
        return erro(HttpStatus.BAD_REQUEST, "Parâmetro inválido",
                "O parâmetro '" + ex.getName() + "' possui um valor inválido: '" + ex.getValue() + "'.");
    }

    // 400 – Parâmetro obrigatório ausente
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleParametroAusente(MissingServletRequestParameterException ex) {
        return erro(HttpStatus.BAD_REQUEST, "Parâmetro ausente",
                "O parâmetro obrigatório '" + ex.getParameterName() + "' não foi informado.");
    }

    // 400 – Validação de campos @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException ex) {
        String detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> "'" + f.getField() + "': " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return erro(HttpStatus.BAD_REQUEST, "Campos inválidos", detalhes);
    }

    // 401 – Não autenticado
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleNaoAutenticado(AuthenticationException ex) {
        return erro(HttpStatus.UNAUTHORIZED, "Acesso não autorizado",
                "Você precisa estar autenticado para acessar este recurso. Faça login e tente novamente.");
    }

    // 403 – Acesso negado
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAcessoNegado(AccessDeniedException ex) {
        return erro(HttpStatus.FORBIDDEN, "Acesso negado",
                "Você não tem permissão para realizar esta operação.");
    }

    // 500 – Erro genérico inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenerico(Exception ex) {
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }

    private ResponseEntity<Map<String, Object>> erro(HttpStatus status, String titulo, String detalhe) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("erro", titulo);
        body.put("mensagem", detalhe);
        return ResponseEntity.status(status).body(body);
    }
}

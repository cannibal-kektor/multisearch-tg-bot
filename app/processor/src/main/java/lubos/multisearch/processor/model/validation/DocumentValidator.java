package lubos.multisearch.processor.model.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lubos.multisearch.processor.model.mongo.SearchDocument;
import lubos.multisearch.processor.exception.ValidationException;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

@Component
public class DocumentValidator extends AbstractMongoEventListener<Object> {

    private final Validator validator;

    public DocumentValidator(LocalValidatorFactoryBean validator) {
        this.validator = validator;
    }

    @Override
    public void onBeforeSave(BeforeSaveEvent<Object> event) {
        Object entity = event.getSource();
        Set<ConstraintViolation<Object>> violations;
        if (entity instanceof SearchDocument document) {
            violations = switch (document.getDocumentType()) {
                case HTML -> validator.validate(document, DocumentValidation.HTML.class);
                case FILE -> validator.validate(document, DocumentValidation.File.class);
            };
        } else {
            violations = validator.validate(entity);
        }
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }
}

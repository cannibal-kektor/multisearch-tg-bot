package lubos.multisearch.processor.model.validation;

import jakarta.validation.groups.Default;

public interface DocumentValidation {

    interface File extends Default {
    }

    interface HTML extends Default{
    }

}

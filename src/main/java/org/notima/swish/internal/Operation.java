package org.notima.swish.internal;

/**
 * Only used internally 
 */
class Operation {
    private String op;
    private String path;
    private String value;

    Operation() {
        // So far, these fields only have one possible value each.
        op = "replace";
        path = "/status";
        value = "cancelled";
    }

    String getOp() {
        return op;
    }

    void setOp(String op) {
        this.op = op;
    }

    String getPath() {
        return path;
    }

    void setPath(String path) {
        this.path = path;
    }

    String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }
}

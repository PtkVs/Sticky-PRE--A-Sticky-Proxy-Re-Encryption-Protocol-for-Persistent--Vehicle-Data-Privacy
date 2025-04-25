package tf.cyber.resourcemanager.pep.data;

import tf.cyber.resourcemanager.pep.mapper.tojava.JavaMapper;
import tf.cyber.resourcemanager.pep.mapper.toxacml.XACMLMapper;

public class XACMLAttribute<T> {
    private final String id;
    private final String dataType;
    private final String category;
    private final T value;

    public XACMLAttribute(String id, String category, T value) {
        this.id = id;
        this.category = category;
        this.dataType = DataTypes.map(value.getClass());
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getDataType() {
        return dataType;
    }

    public T getValue() {
        return value;
    }

    public String getXACMLValue() {
        return XACMLMapper.map(value);
    }

    public static XACMLAttribute<?> of(String id, String dataType, String category, String value) {
        Class<?> type = DataTypes.map(dataType);
        Object mapped = JavaMapper.map(type, value);

        return new XACMLAttribute<>(id, category, mapped);
    }
}

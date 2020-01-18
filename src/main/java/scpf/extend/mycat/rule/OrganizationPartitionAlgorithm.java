package scpf.extend.mycat.rule;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import io.mycat.route.function.AbstractPartitionAlgorithm;

public class OrganizationPartitionAlgorithm extends AbstractPartitionAlgorithm {

    private static int DEFAULT_DB = 0;
    /**
     * 实际分片计算规则的函数
     * @param columnValue 传入的数据字段值
     * @return 返回dataNode的标号
     */
    @Override
    public Integer calculate(String columnValue) {
        return DEFAULT_DB;
    }
}

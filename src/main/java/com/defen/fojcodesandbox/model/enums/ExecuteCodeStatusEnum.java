package com.defen.fojcodesandbox.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 执行代码状态枚举
 */
@Getter
public enum ExecuteCodeStatusEnum {

    SUCCESS("执行成功", 0),
    COMPILE_ERROR("编译失败", 1),
    RUNTIME_ERROR("执行失败", 2),
    TIME_LIMIT_EXCEEDED("超出时间限制", 3);

    private final String text;

    private final Integer value;

    ExecuteCodeStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static ExecuteCodeStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ExecuteCodeStatusEnum anEnum : ExecuteCodeStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}

package org.ww.service.enumHandler;

import com.baomidou.mybatisplus.core.enums.IEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Configurable;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configurable
public class EnumValueObjHandler extends BaseTypeHandler<IEnum> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, IEnum iEnum, JdbcType jdbcType) throws SQLException {
        preparedStatement.setObject(i,iEnum.getValue());
    }

    @Override
    public IEnum getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return (IEnum) resultSet.getObject(s);
    }

    @Override
    public IEnum getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return (IEnum) resultSet.getObject(i);
    }

    @Override
    public IEnum getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return (IEnum) callableStatement.getObject(i);
    }

}

package org.nd4j.linalg.api.ops.impl.transforms.custom;

import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.base.Preconditions;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ops.DynamicCustomOp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Broadcastable element-wise power operation: x[i]^y[i]
 *
 * @author Alex Black
 */
public class Pow extends DynamicCustomOp {

    public Pow(SameDiff sameDiff, SDVariable x, SDVariable y){
        super(sameDiff, new SDVariable[]{x, y});
    }

    public Pow(){ }

    @Override
    public String opName(){
        return "Pow";
    }

    @Override
    public String tensorflowName(){
        return "Pow";
    }

    @Override
    public List<SDVariable> doDiff(List<SDVariable> f1) {
        //TODO: replace this with discrete op once available: https://github.com/deeplearning4j/deeplearning4j/issues/7461
        //If y=a^b, then:
        //dL/da = b*a^(b-1) * dL/dy
        //dL/db = a^b * log(a) * dL/dy

        SDVariable a = arg(0);
        SDVariable b = arg(1);
        SDVariable dlda = b.mul(sameDiff.math().pow(a,b.sub(1))).mul(f1.get(0));
        SDVariable dldb = outputVariable().mul(sameDiff.math().log(a)).mul(f1.get(0));
        return Arrays.asList(dlda, dldb);
    }

    @Override
    public List<DataType> calculateOutputDataTypes(List<DataType> dataTypes){
        Preconditions.checkState(dataTypes != null && dataTypes.size() == 2, "Expected exactly 2 input datatypes for %s, got %s", getClass(), dataTypes);
        return Collections.singletonList(dataTypes.get(0));
    }
}

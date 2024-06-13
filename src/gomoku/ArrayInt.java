package gomoku;

import java.util.Arrays;
import java.util.Objects;

public class ArrayInt {
    public int[] array;
    ArrayInt(int[] array){
        this.array = array;
    }
    public void copy(){
        int[] array2 = array;
        array = new int[array2.length];
        System.arraycopy(array2, 0, array, 0, array2.length);
    }
    @Override
    public boolean equals(Object obj) {
        int[] array2;
        if (obj instanceof int[])
            array2 = (int[])obj;
        else if (obj instanceof ArrayInt)
            array2 = ((ArrayInt)obj).array;
        else
            return false;
        //System.out.println(Arrays.toString(array) + " | " + Arrays.toString(array2));
        if (array.length != array2.length)
            return false;
        for (int i = 0; i < array.length; i++)
            if (array[i] != array2[i])
                return false;
        return true;
    }
    public int hashCode() {
        return Objects.hash(array[0], array[1], array[2], array[3], array[4], array[5], array[6],
                array[7], array[8], array[9], array[10], array[11], array[12], array[13], array[14]);
    }
}

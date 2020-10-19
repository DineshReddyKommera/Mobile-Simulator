package com.example.cacheandvirtualmemorysimulator.ui.home;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cacheandvirtualmemorysimulator.R;

import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment {

    RadioGroup writeThroughBack;
    RadioButton mRadWriteBack;
    RadioButton mRadWriteThrough;
    RadioButton mRadWriteOnAllocate;
    RadioButton mRadWriteAround;
    ArrayAdapter<String> instructionTypes;
    RadioGroup writeAllocateAround;
    EditText mEdtCacheSize;
    EditText mEdtMemorySize;
    EditText mEdtOffsetBits;
    Button mBtnReset;
    Button mBtnDesignSubmit;
    Spinner mSprInstType;
    EditText mEdtAddress;
    Button mBtnGetRandom;
    Button mBtnInstSubmit;
    TextView mTxtInstUpdates;
    Button mBtnNextStep;
    Button mBtnFastForward;
    int cacheSize;
    int memorySize;
    int offsetBits;
    int tagBitSize;
    int blockBitSize;
    int cacheBitSize;
    int indexBitSize;
    int memoryBitSize;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        writeAllocateAround  = root.findViewById(R.id.write_allocate_around);
        writeThroughBack = root.findViewById(R.id.write_back_through);
        mEdtCacheSize = root.findViewById(R.id.m_edt_c_input_size);
        mEdtMemorySize = root.findViewById(R.id.m_edt_c_memory_size);
        mEdtOffsetBits = root.findViewById(R.id.m_edt_c_offset_bits);
        mBtnReset = root.findViewById(R.id.m_btn_c_reset);
        mBtnDesignSubmit = root.findViewById(R.id.m_btn_c_design_submit);
        mSprInstType = root.findViewById(R.id.m_c_instruction_type);
        List<String> instructions = new LinkedList<>();
        instructions.add("Load");
        instructions.add("store");
        instructionTypes = new ArrayAdapter<>(this.getContext(),android.R.layout.simple_spinner_dropdown_item,instructions);
        instructionTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSprInstType.setAdapter(instructionTypes);
        mSprInstType.setSelection(0);

        mEdtAddress = root.findViewById(R.id.m_edt_c_address);
        mTxtInstUpdates = root.findViewById(R.id.m_txt_c_instruction);
        mTxtInstUpdates.setMovementMethod(new ScrollingMovementMethod());
        mRadWriteBack = root.findViewById(R.id.write_back);
        mRadWriteThrough = root.findViewById(R.id.write_through);
        mRadWriteOnAllocate = root.findViewById(R.id.write_on_allocate);
        mRadWriteAround = root.findViewById(R.id.write_around);

        mBtnDesignSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cacheSize = Integer.parseInt(mEdtCacheSize.getText().toString());
                memorySize = Integer.parseInt(mEdtMemorySize.getText().toString());
                offsetBits = Integer.parseInt(mEdtOffsetBits.getText().toString());
                int cacheSizeCheck = Integer.highestOneBit(cacheSize);
                int memorySizeCheck = Integer.highestOneBit(memorySize);
                cacheBitSize = (int)(Math.log(cacheSize)/Math.log(2));
                memoryBitSize = (int)(Math.log(memorySize)/Math.log(2));
                tagBitSize = memoryBitSize-offsetBits-cacheBitSize;
                blockBitSize = tagBitSize + cacheSizeCheck;
                indexBitSize = cacheBitSize - offsetBits;
                if(cacheSize!=cacheSizeCheck){
                    mEdtCacheSize.setError("Cache size should be power of 2");
                }else if(memorySize!=memorySizeCheck){
                    mEdtMemorySize.setError("Memory size should be power of two");
                }else if(offsetBits>cacheBitSize){
                    mEdtOffsetBits.setError("OffsetBits for given input should be less than or equal to"+cacheSizeCheck);
                }else{
                    mTxtInstUpdates.setText("Instructions:\n" +
                            "Instruction Length = log2("+memorySize+") = "+memoryBitSize+" bits\n" +
                            "Offset ="+offsetBits+" \n" +
                            "Index bits = log2("+cacheSize+")-"+offsetBits+" = "+indexBitSize+" bits\n" +
                            "Tag = "+memoryBitSize+" bits - "+offsetBits+" bits - "+cacheBitSize+" bits = "+ tagBitSize +" bits\n" +
                            "Block = "+ tagBitSize +" bits + "+cacheBitSize+" bits = "+ blockBitSize +" bits\n" +
                            "Please submit Instruction.");
                }
            }
        });

        return root;
    }
}
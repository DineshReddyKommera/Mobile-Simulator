package com.example.cacheandvirtualmemorysimulator.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cacheandvirtualmemorysimulator.POJO.CacheRecord;
import com.example.cacheandvirtualmemorysimulator.R;

import java.math.BigDecimal;
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
    TableLayout mTableLayout;
    int cacheSize;
    int memorySize;
    int offsetBits;
    int tagBitSize;
    int blockBitSize;
    int cacheBitSize;
    int indexBitSize;
    int memoryBitSize;
    CacheRecord[] cacheTable;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        writeAllocateAround  = root.findViewById(R.id.write_allocate_around);
        writeThroughBack = root.findViewById(R.id.write_back_through);
        mRadWriteBack = root.findViewById(R.id.write_back);
        mRadWriteThrough = root.findViewById(R.id.write_through);
        mRadWriteOnAllocate = root.findViewById(R.id.write_on_allocate);
        mRadWriteAround = root.findViewById(R.id.write_around);

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

        mBtnGetRandom = root.findViewById(R.id.m_c_random_btn);
        mBtnInstSubmit = root.findViewById(R.id.m_cache_submit_button);

        mTxtInstUpdates = root.findViewById(R.id.m_txt_c_instruction);
        mTxtInstUpdates.setMovementMethod(new ScrollingMovementMethod());

        mTableLayout = root.findViewById(R.id.m_c_cache_table);
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
                tagBitSize = memoryBitSize-cacheBitSize;
                indexBitSize = cacheBitSize - offsetBits;
                blockBitSize = tagBitSize + indexBitSize;
                if(cacheSize!=cacheSizeCheck){
                    mEdtCacheSize.setError("Cache size should be power of 2");
                }else if(memorySize!=memorySizeCheck){
                    mEdtMemorySize.setError("Memory size should be power of two");
                }else if(offsetBits>cacheBitSize){
                    mEdtOffsetBits.setError("OffsetBits for given input should be less than or equal to"+cacheSizeCheck);
                }else{
                    //TODO: If time permits restate calculated instructions more user friendly
                    mTxtInstUpdates.setText("Instructions:\n" +
                            "Cache Bits =log2("+cacheSize+") = "+cacheBitSize+ " bits\n" +
                            "Instruction Length = log2("+memorySize+") = "+memoryBitSize+" bits\n" +
                            "Offset ="+offsetBits+" \n" +
                            "Index bits = log2("+cacheSize+")-"+offsetBits+"(Offset) = "+indexBitSize+" bits\n" +
                            "Tag = "+memoryBitSize+" bits - "+cacheBitSize+" bits (cache bit size) = "+ tagBitSize +" bits\n" +
                            "Block = "+ tagBitSize +" bits (tag) + "+indexBitSize+" bits (index bit size) = "+ blockBitSize +" bits\n" +
                            "Please submit Instruction.");
                    cacheTable = new CacheRecord[(int)Math.pow(2,indexBitSize)];
                    for(int i=0;i<Math.pow(2,indexBitSize);i++){
                        cacheTable[i] = new CacheRecord(i,false,"-","0",false);
                    }
                    mTableLayout.removeAllViews();
                    TextView textSpacer = null;
                    int leftRowMargin=0;
                    int topRowMargin=0;
                    int rightRowMargin=0;
                    int bottomRowMargin = 0;
                    for(int i = -1; i<Math.pow(2,indexBitSize); i ++) {
                        CacheRecord cacheRecord = null;
                        if (i > -1)
                            cacheRecord = cacheTable[i];
                        else {
                            textSpacer = new TextView(getContext());
                            textSpacer.setText("");
                        }
                        // data columns
                        final TextView tv = new TextView(getContext());
                        tv.setLayoutParams(new
                                TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tv.setGravity(Gravity.LEFT);
                        tv.setPadding(5, 15, 0, 15);
                        if (i == -1) {
                            tv.setText("Index#");
                            tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
                        }
                        else {
                            tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                            tv.setText(String.valueOf(cacheRecord.getIndex()));
                        }
                        //tv.setTextSize(TypedValue.COMPLEX_UNIT_PX);
                        final TextView tv2 = new TextView(getContext());
                        if (i == -1) {
                            tv2.setLayoutParams(new
                                    TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                        }
                        else {
                            tv2.setLayoutParams(new
                                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.MATCH_PARENT));
                        }
                        tv2.setGravity(Gravity.LEFT);
                        tv2.setPadding(5, 15, 0, 15);
                        if (i == -1) {
                            tv2.setText("Valid");
                            tv2.setBackgroundColor(Color.parseColor("#f7f7f7"));
                        }
                        else {
                            tv2.setBackgroundColor(Color.parseColor("#ffffff"));
                            tv2.setTextColor(Color.parseColor("#000000"));
                            tv2.setText(String.valueOf(cacheRecord.isValid()?1:0));
                        }
                        final TextView tv3 = new TextView(getContext());
                        if (i == -1) {
                            tv3.setLayoutParams(new
                                    TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                        }
                        else {
                            tv3.setLayoutParams(new
                                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.MATCH_PARENT));
                        }
                        tv3.setGravity(Gravity.LEFT);
                        tv3.setPadding(5, 15, 0, 15);
                        if (i == -1) {
                            tv3.setText("Tag");
                            tv3.setBackgroundColor(Color.parseColor("#f7f7f7"));
                        }
                        else {
                            tv3.setBackgroundColor(Color.parseColor("#ffffff"));
                            tv3.setTextColor(Color.parseColor("#000000"));
                            tv3.setText(cacheRecord.getTag());
                        }

                        final TextView tv4 = new TextView(getContext());
                        if (i == -1) {
                            tv4.setLayoutParams(new
                                    TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                        }
                        else {
                            tv4.setLayoutParams(new
                                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.MATCH_PARENT));
                        }
                        tv4.setGravity(Gravity.LEFT);
                        tv4.setPadding(5, 15, 0, 15);
                        if (i == -1) {
                            tv4.setText("Data (Hex)");
                            tv4.setBackgroundColor(Color.parseColor("#f7f7f7"));
                        }
                        else {
                            tv4.setBackgroundColor(Color.parseColor("#ffffff"));
                            tv4.setTextColor(Color.parseColor("#000000"));
                            tv4.setText(cacheRecord.getData());
                        }

                        final TextView tv5 = new TextView(getContext());
                        if (i == -1) {
                            tv5.setLayoutParams(new
                                    TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                        }
                        else {
                            tv5.setLayoutParams(new
                                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.MATCH_PARENT));
                        }
                        tv5.setGravity(Gravity.LEFT);
                        tv5.setPadding(5, 15, 0, 15);
                        if (i == -1) {
                            tv5.setText("Dirty bit");
                            tv5.setBackgroundColor(Color.parseColor("#f7f7f7"));
                        }
                        else {
                            tv5.setBackgroundColor(Color.parseColor("#ffffff"));
                            tv5.setTextColor(Color.parseColor("#000000"));
                            tv5.setText(String.valueOf(cacheRecord.isDirtyBit()?1:0));
                        }
                        // add table row
                        final TableRow tr = new TableRow(getContext());
                        tr.setId(i + 1);
                        TableLayout.LayoutParams trParams = new
                                TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT);
                        trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                                bottomRowMargin);
                        tr.setPadding(0,0,0,0);
                        tr.setLayoutParams(trParams);
                        tr.addView(tv);
                        tr.addView(tv2);
                        tr.addView(tv3);
                        tr.addView(tv4);
                        tr.addView(tv5);
                        if (i > -1) {
                            tr.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    TableRow tr = (TableRow) v;
                                }
                            });
                        }
                        mTableLayout.addView(tr, trParams);
                        if (i > -1) {
                            // add separator row
                            final TableRow trSep = new TableRow(getContext());
                            TableLayout.LayoutParams trParamsSep = new
                                    TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT);
                            trParamsSep.setMargins(leftRowMargin, topRowMargin,
                                    rightRowMargin, bottomRowMargin);
                            trSep.setLayoutParams(trParamsSep);
                            TextView tvSep = new TextView(getContext());
                            TableRow.LayoutParams tvSepLay = new
                                    TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT);
                            tvSepLay.span = 4;
                            tvSep.setLayoutParams(tvSepLay);
                            tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                            tvSep.setHeight(1);
                            trSep.addView(tvSep);
                            mTableLayout.addView(trSep, trParamsSep);
                        }
                    }
                }
            }
        });

        mBtnGetRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int randomAddress =  (int)(Math.random()*(memorySize)+1);
                mEdtAddress.setText(Integer.toHexString(randomAddress));
            }
        });

        return root;
    }
}
package com.example.cacheandvirtualmemorysimulator.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cacheandvirtualmemorysimulator.POJO.CacheRecord;
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
    TableLayout mCacheTableLayout;
    TableLayout mMemoryBlockLayout;
    TableLayout mInstructionTableLayout;
    int cacheSize;
    int memorySize;
    int offsetBits;
    int tagBitSize;
    int blockBitSize;
    int cacheBitSize;
    int indexBitSize;
    int memoryBitSize;
    int addressInstruction;
    String currentTagBinary;
    String currentIndexBinary;
    String currentOffsetBinary;
    String currentBlockBinary;
    int currentBlockDecimal;
    int currentIndexDecimal;
    String currentBlockHex;
    int stepsCount=0;
    int totalInstructions = 0;
    int totalHitInstructions = 0;

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
        mMemoryBlockLayout = root.findViewById(R.id.tlGridTable);
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
        mBtnGetRandom.setEnabled(false);
        mBtnInstSubmit = root.findViewById(R.id.m_cache_submit_button);
        mBtnInstSubmit.setEnabled(false);
        mBtnNextStep = root.findViewById(R.id.m_btn_c_next_move);
        mBtnNextStep.setEnabled(false);
        mBtnFastForward = root.findViewById(R.id.m_btn_c_fast_forward);
        mBtnFastForward.setEnabled(false);
        mTxtInstUpdates = root.findViewById(R.id.m_txt_c_instruction);
        mTxtInstUpdates.setMovementMethod(new ScrollingMovementMethod());

        mInstructionTableLayout = root.findViewById(R.id.m_c_instruction_table);

        mCacheTableLayout = root.findViewById(R.id.m_c_cache_table);
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
                    mTxtInstUpdates.setText("Information:\n" +
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
                    stepsCount=1;
                    mBtnGetRandom.setEnabled(true);
                    mBtnInstSubmit.setEnabled(true);
                    updateCacheTable(cacheTable,-2, false, false, false);
                    updateMemoryTable(blockBitSize,offsetBits);
                }
            }
        });

        mBtnGetRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressInstruction =  (int)(Math.random()*(memorySize)+1);
                mEdtAddress.setText(Integer.toHexString(addressInstruction));
            }
        });

        mBtnInstSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String binaryInstruction = String
                        .format("%"+ memoryBitSize+"s",Integer.toBinaryString(addressInstruction))
                        .replace(' ','0');
                currentTagBinary = binaryInstruction.substring(0,tagBitSize);
                currentIndexBinary = binaryInstruction.substring(tagBitSize, blockBitSize);
                currentIndexDecimal = Integer.parseInt(currentIndexBinary,2);
                currentOffsetBinary = binaryInstruction.substring(blockBitSize);

                currentBlockBinary = binaryInstruction.substring(0,blockBitSize);
                currentBlockDecimal = Integer.parseInt(currentBlockBinary,2);
                currentBlockHex = Integer.toHexString(currentBlockDecimal);
                mTxtInstUpdates.setText("Information:" +
                        "\nThe instruction has been converted from hex to binary and allocated to tag, index, and offset respectively");
                updateInstructionBreakdown(currentTagBinary,currentIndexBinary,currentOffsetBinary,false,false);
                stepsCount=1;
                totalInstructions++;
                mBtnNextStep.setEnabled(true);
                mBtnFastForward.setEnabled(true);
            }
        });

        mBtnFastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTxtInstUpdates.setText("Information:" +
                                "\nThe cycle has been completed. Please submit another instructions");
                updateCacheTable(cacheTable,-2, false, false, false);
            }
        });

        mBtnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTxtInstUpdates.setText("Information:\n");
                if(mSprInstType.getSelectedItem().toString().equalsIgnoreCase("store")) {
                    if(stepsCount==1){
                            mTxtInstUpdates.append(mRadWriteBack.isChecked()
                                    ?"Write back policy is adopted. Cache will be updated with dirty bit"
                                    :"Write Through Policy is adopted. Memory and Cache will be updated at the same time.");
                        stepsCount++;
                    } else if(stepsCount==2){
                        mTxtInstUpdates.append("Search is formed to determine whether the requested address is available in cache table");
                        stepsCount++;
                    } else if(stepsCount==3){
                        if(cacheTable[currentIndexDecimal].getTag().equals(currentTagBinary)){
                            mTxtInstUpdates.append("Requested address is found in cache table");
                            stepsCount++;
                        }else{
                            mTxtInstUpdates.append("Requested address is not found in cache table");
                            if(mRadWriteAround.isChecked()){
                                stepsCount=5;
                            }else{
                                stepsCount++;
                            }
                        }

                    } else if(stepsCount==4){
                        if(cacheTable[currentIndexDecimal].getTag().equals(currentTagBinary)){
                            if(mRadWriteBack.isChecked()) {
                                mTxtInstUpdates.append("Highlighted cache is updated with dirty bit = 1");
                                cacheTable[currentIndexDecimal].setDirtyBit(true);
                            }else {
                                mTxtInstUpdates.append("Highlighted memory block and cache is updated");
                            }
                        }else{
                            mTxtInstUpdates.append("Cache does not contain requested tag. ");
                            if(mRadWriteOnAllocate.isChecked()) {
                                mTxtInstUpdates.append("Data is loaded and content is updated based on Write On Allocate policy");
                                CacheRecord cacheRecord = new CacheRecord(currentIndexDecimal,true,currentTagBinary,"BLOCK "+currentBlockHex.toUpperCase()+" WORD 0 - "+((int)Math.pow(2,indexBitSize)-1),false);
                                cacheTable[currentIndexDecimal]=cacheRecord;
                            }else{
                                mTxtInstUpdates.append(" Only memory block is updated based on Write Around Policy.");
                            }
                        }
                        stepsCount = 6;
                    } else if(stepsCount==5){
                        stepsCount=0;
                        mBtnNextStep.setEnabled(false);
                        mBtnFastForward.setEnabled(false);
                    }
                    updateCacheTable(cacheTable,currentIndexDecimal, false, false, false);
                }else{
                    if(stepsCount==1){
                        mTxtInstUpdates.append("Index requested will be searched in cache as highlighted in yellow");
                        updateInstructionBreakdown(currentTagBinary,currentIndexBinary,currentOffsetBinary,false,true);
                        updateCacheTable(cacheTable,currentIndexDecimal, false, false, false);
                        stepsCount++;
                    }else if(stepsCount==2){
                        mTxtInstUpdates.append("Valid bit will be obtained and analysed.");
                        updateCacheTable(cacheTable,currentIndexDecimal, true, false, false);
                        stepsCount++;
                    } else if(stepsCount==3){
                        if(cacheTable[currentIndexDecimal].isValid()) {
                            mTxtInstUpdates.append("Valid bit is 1, therefore we should look into the tag.");
                            if(cacheTable[currentIndexDecimal].getTag().equals(currentTagBinary)){
                                mTxtInstUpdates.append("Requested Tag and cached tag is the same. Therefore, CACHE HIT");
                                totalHitInstructions++;
                                stepsCount=5;
                            }else{
                                mTxtInstUpdates.append("Requested Tag and cached tag is NOT the same. Therefore, CACHE MISS");
                                stepsCount++;
                            }
                            updateInstructionBreakdown(currentTagBinary,currentIndexBinary,currentOffsetBinary,true,true);
                            updateCacheTable(cacheTable,currentIndexDecimal, false,true,false);
                        }else{
                            mTxtInstUpdates.append("Valid bit is 0, therefore CACHE MISS is obtained. Cache is updated with the new dataset");
                            stepsCount=5;
                        }
                    } else if(stepsCount==4){
                        updateInstructionBreakdown(currentTagBinary,currentIndexBinary,currentOffsetBinary,false,true);
                        mTxtInstUpdates.append("Cache replace the old index.");
                        if(cacheTable[currentIndexDecimal].isDirtyBit()){
                            mTxtInstUpdates.append("Since dirty bit is 1, Memory will be updated.");
                        }else{
                            mTxtInstUpdates.append("Since dirty bit is 0, there is no additional operation required.");
                        }
                        updateCacheTable(cacheTable,currentIndexDecimal, false, false, true);
                        stepsCount++;
                    } else if(stepsCount==5){
                        mTxtInstUpdates.append("Cache table is updated accordingly. Block "+currentBlockHex+" with offset 0 to "+(Math.pow(2,offsetBits)-1)+" is transferred to cache");
                        CacheRecord cacheRecord = new CacheRecord(currentIndexDecimal,true,currentTagBinary
                                ,"BLOCK "+currentBlockHex.toUpperCase()+" WORD 0 - "+((int)Math.pow(2,indexBitSize)-1),false);
                        cacheTable[currentIndexDecimal] = cacheRecord;
                        updateCacheTable(cacheTable,currentIndexDecimal, false, false, false);
                        stepsCount=6;
                    } else if(stepsCount==6){
                        mTxtInstUpdates.append("The cycle has been completed. Please submit another instructions");
                        stepsCount=0;
                        updateCacheTable(cacheTable,-2, false, false, false);
                        updateInstructionBreakdown(currentTagBinary,currentIndexBinary,currentOffsetBinary,false,false);
                        mBtnNextStep.setEnabled(false);
                        mBtnFastForward.setEnabled(false);
                    }
                }
            }
        });

        return root;
    }

    void updateInstructionBreakdown(String tag,String index, String offset,boolean highLightTag,boolean highLightIndex){
        mInstructionTableLayout.removeAllViews();
        int leftRowMargin = 0;
        int topRowMargin = 0;
        int rightRowMargin = 0;
        int bottomRowMargin = 0;
        final TextView tv = new TextView(getContext());
        tv.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv.setGravity(Gravity.LEFT);
        tv.setPadding(5, 15, 0, 15);
        tv.setText("Tag");
        tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
        final TextView tv2 = new TextView(getContext());
            tv2.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        tv2.setGravity(Gravity.LEFT);
        tv2.setPadding(5, 15, 0, 15);
        tv2.setText("Index");
        tv2.setBackgroundColor(Color.parseColor("#f0f0f0"));
        final TextView tv3 = new TextView(getContext());
        tv3.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv3.setGravity(Gravity.LEFT);
        tv3.setPadding(5, 15, 0, 15);
        tv3.setText("Offset");
        tv3.setBackgroundColor(Color.parseColor("#f0f0f0"));
        final TableRow tr = new TableRow(getContext());
        tr.setId(0);
        TableLayout.LayoutParams trParams = new
                TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                bottomRowMargin);
        tr.setPadding(0, 0, 0, 0);
        tr.setLayoutParams(trParams);
        tr.addView(tv);
        tr.addView(tv2);
        tr.addView(tv3);

        final TextView tv4 = new TextView(getContext());
        tv4.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv4.setGravity(Gravity.LEFT);
        tv4.setPadding(5, 15, 0, 15);
        tv4.setText(tag);
        if(highLightTag)
            tv4.setBackgroundColor(ContextCompat.getColor(this.getActivity(),R.color.tag));
        tv4.setTextColor(Color.parseColor("#000000"));
        final TextView tv5 = new TextView(getContext());
        tv5.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv5.setGravity(Gravity.LEFT);
        tv5.setPadding(5, 15, 0, 15);
        tv5.setText(index);
        if(highLightIndex)
            tv5.setBackgroundColor(Color.parseColor("#ffff00"));
        tv5.setTextColor(Color.parseColor("#000000"));
        final TextView tv6 = new TextView(getContext());
        tv6.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv6.setGravity(Gravity.LEFT);
        tv6.setPadding(5, 15, 0, 15);
        tv6.setText(offset);
        //tv6.setBackgroundColor(Color.parseColor("#ffffff"));
        tv6.setTextColor(Color.parseColor("#000000"));
        final TableRow tr1 = new TableRow(getContext());
        tr1.setId(1);
        trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                bottomRowMargin);
        tr1.setPadding(0, 0, 0, 0);
        tr1.setLayoutParams(trParams);
        tr1.addView(tv4);
        tr1.addView(tv5);
        tr1.addView(tv6);

        mInstructionTableLayout.addView(tr, trParams);
        mInstructionTableLayout.addView(tr1, trParams);
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
        mInstructionTableLayout.addView(trSep, trParamsSep);
    }

    void updateMemoryTable(int blocks,int words){
        mMemoryBlockLayout.removeAllViews();
        int leftRowMargin = 0;
        int topRowMargin = 0;
        int rightRowMargin = 0;
        int bottomRowMargin = 0;

        for (int i = 0; i < Math.pow(2,blocks); i++) {
            final TableRow tr = new TableRow(getContext());
            tr.setId(i);
            TableLayout.LayoutParams trParams = new
                    TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                    bottomRowMargin);
            tr.setPadding(0, 0, 0, 0);
            tr.setLayoutParams(trParams);
            TextView[] textViews = new TextView[(int) Math.pow(words,2)];
            for(int j=0;j < Math.pow(2,words);j++){
                textViews[j] = new TextView(getContext());
                textViews[j].setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                textViews[j].setGravity(Gravity.LEFT);
                textViews[j].setText("B."+Integer.toHexString(i).toUpperCase()+" W."+Integer.toHexString(j).toUpperCase());
                textViews[j].setPadding(5, 15, 0, 15);
                tr.addView(textViews[j]);
            }
            mMemoryBlockLayout.addView(tr, trParams);
        }

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
        mMemoryBlockLayout.addView(trSep, trParamsSep);
    }

    void updateCacheTable(CacheRecord[] cacheTable,int highLightRecord,boolean highLightValidBit, boolean highLightTag,boolean highLightDirtyBit){
            mCacheTableLayout.removeAllViews();
            TextView textSpacer = null;
            int leftRowMargin = 0;
            int topRowMargin = 0;
            int rightRowMargin = 0;
            int bottomRowMargin = 0;
            for (int i = -1; i < Math.pow(2, indexBitSize); i++) {
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
                } else {
//                    if(highLightRecord==i)
//                        tv.setBackgroundColor(Color.parseColor("#ffff00"));
//                    else
//                        tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                    tv.setText(String.valueOf(cacheRecord.getIndex()));
                }
                //tv.setTextSize(TypedValue.COMPLEX_UNIT_PX);
                final TextView tv2 = new TextView(getContext());
                if (i == -1) {
                    tv2.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                } else {
                    tv2.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.MATCH_PARENT));
                }
                tv2.setGravity(Gravity.LEFT);
                tv2.setPadding(5, 15, 0, 15);
                if (i == -1) {
                    tv2.setText("Valid");
                    tv2.setBackgroundColor(Color.parseColor("#f7f7f7"));
                } else {
                    if(highLightRecord==i && highLightValidBit)
                        tv2.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.validBit));
                    tv2.setTextColor(Color.parseColor("#000000"));
                    tv2.setText(String.valueOf(cacheRecord.isValid() ? 1 : 0));
                }
                final TextView tv3 = new TextView(getContext());
                if (i == -1) {
                    tv3.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                } else {
                    tv3.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.MATCH_PARENT));
                }
                tv3.setGravity(Gravity.LEFT);
                tv3.setPadding(5, 15, 0, 15);
                if (i == -1) {
                    tv3.setText("Tag");
                    tv3.setBackgroundColor(Color.parseColor("#f7f7f7"));
                } else {
                    if(highLightRecord==i && highLightTag)
                        tv3.setBackgroundColor(ContextCompat.getColor(this.getActivity(),R.color.tag));
                    tv3.setTextColor(Color.parseColor("#000000"));
                    tv3.setText(cacheRecord.getTag());
                }

                final TextView tv4 = new TextView(getContext());
                if (i == -1) {
                    tv4.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                } else {
                    tv4.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.MATCH_PARENT));
                }
                tv4.setGravity(Gravity.LEFT);
                tv4.setPadding(5, 15, 0, 15);
                if (i == -1) {
                    tv4.setText("Data (Hex)");
                    tv4.setBackgroundColor(Color.parseColor("#f7f7f7"));
                } else {
//                    if(highLightRecord==i)
//                        tv4.setBackgroundColor(Color.parseColor("#ffff00"));
//                    else
//                        tv4.setBackgroundColor(Color.parseColor("#ffffff"));
                    tv4.setTextColor(Color.parseColor("#000000"));
                    tv4.setText(cacheRecord.getData());
                }

                final TextView tv5 = new TextView(getContext());
                if (i == -1) {
                    tv5.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                } else {
                    tv5.setLayoutParams(new
                            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.MATCH_PARENT));
                }
                tv5.setGravity(Gravity.LEFT);
                tv5.setPadding(5, 15, 0, 15);
                if (i == -1) {
                    tv5.setText("Dirty bit");
                    tv5.setBackgroundColor(Color.parseColor("#f7f7f7"));
                } else {
//                    if(highLightRecord==i)
//                        tv5.setBackgroundColor(Color.parseColor("#ffff00"));
//                    else
//                        tv5.setBackgroundColor(Color.parseColor("#ffffff"));
                    tv5.setTextColor(Color.parseColor("#000000"));
                    tv5.setText(String.valueOf(cacheRecord.isDirtyBit() ? 1 : 0));
                }
                // add table row
                final TableRow tr = new TableRow(getContext());
                tr.setId(i + 1);
                TableLayout.LayoutParams trParams = new
                        TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                        bottomRowMargin);
                tr.setPadding(0, 0, 0, 0);
                tr.setLayoutParams(trParams);
                tr.addView(tv);
                tr.addView(tv2);
                tr.addView(tv3);
                tr.addView(tv4);
                tr.addView(tv5);
                if(highLightRecord==i)
                        tr.setBackgroundColor(Color.parseColor("#ffff00"));
                if (i > -1) {
                    tr.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            TableRow tr = (TableRow) v;
                        }
                    });
                }
                mCacheTableLayout.addView(tr, trParams);
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
                    mCacheTableLayout.addView(trSep, trParamsSep);
                }
            }
        }
}
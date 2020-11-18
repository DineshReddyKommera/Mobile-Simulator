package com.example.cacheandvirtualmemorysimulator.ui.gallery;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cacheandvirtualmemorysimulator.POJO.PageTableRecord;
import com.example.cacheandvirtualmemorysimulator.POJO.PhysicalMemoryRecord;
import com.example.cacheandvirtualmemorysimulator.POJO.TLBRecord;
import com.example.cacheandvirtualmemorysimulator.R;

public class GalleryFragment extends Fragment {

    private static final String TAG ="SearchPG:" ;
    private GalleryViewModel galleryViewModel;
    EditText mEdtPhysicalPageSize;
    EditText mEdtVirtualMemorySize;
    EditText mEdtOffsetBits;
    EditText mTLBEntries;
    Button mBtnReset;
    Button mBtnDesignSubmit;
    Button mBtnGetRandom;
    Button mBtnInstSubmit;
    Button mBtnNextStep;
    Button mBtnFastForward;
    EditText mEdtAddress;
    TextView mTxtInstUpdates;
    TableLayout mTlbTableLayout;
    TableLayout mInstructionTableLayout;
    TableLayout mPhysicalMemoryTableLayout,mPageTableLayout;
    int physicalPageSize;
    int virtualMemorySize;
    int  offsetBits;
    int tlbEntries;
    int virtualMemoryBitSize;
    int addressInstruction;
    int stepNumber=1;
    String currentPageBinary;
    String currentOffsetBinary;

    boolean tlbhit;
    boolean pagetablehit;
    TLBRecord tlbrecords[];
    PhysicalMemoryRecord physicalMemoryRecords[];
    PageTableRecord pageTableRecords[];
    int tlbCurrentCounter=0;
    int phyCurrentCounter=0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        mEdtPhysicalPageSize = root.findViewById(R.id.m_edt_c_input_size);
        mEdtVirtualMemorySize = root.findViewById(R.id.m_edt_c_memory_size);
        mEdtOffsetBits = root.findViewById(R.id.m_edt_c_offset_bits);
        mTLBEntries = root.findViewById(R.id.m_edt_c_tlb_bits);
        mBtnReset = root.findViewById(R.id.m_btn_c_reset);
        mBtnDesignSubmit = root.findViewById(R.id.m_btn_c_design_submit);
        mBtnInstSubmit = root.findViewById(R.id.m_cache_submit_button);
        mTlbTableLayout = root.findViewById(R.id.m_c_tlb_table);
        mInstructionTableLayout = root.findViewById(R.id.m_c_instruction_table);
        mPhysicalMemoryTableLayout = root.findViewById(R.id.m_c_phy_table);
        mPageTableLayout = root.findViewById(R.id.m_c_page_table);
        mTxtInstUpdates = root.findViewById(R.id.m_txt_c_instruction);
        mTxtInstUpdates.setMovementMethod(new ScrollingMovementMethod());
        mEdtAddress = root.findViewById(R.id.m_edt_c_address);
        mBtnGetRandom = root.findViewById(R.id.m_c_random_btn);
        mBtnNextStep = root.findViewById(R.id.m_btn_c_next_move);
        mBtnFastForward = root.findViewById(R.id.m_btn_c_fast_forward);
        mBtnFastForward.setEnabled(false);
        mBtnNextStep.setEnabled(false);
        mBtnGetRandom.setEnabled(false);
        mBtnInstSubmit.setEnabled(false);
        mBtnDesignSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                physicalPageSize = Integer.parseInt(mEdtPhysicalPageSize.getText().toString());
                virtualMemorySize = Integer.parseInt(mEdtVirtualMemorySize.getText().toString());
                offsetBits = Integer.parseInt(mEdtOffsetBits.getText().toString());
                tlbEntries = Integer.parseInt(mTLBEntries.getText().toString());
                int physicalSizeCheck = Integer.highestOneBit(physicalPageSize);
                int virtualSizeCheck = Integer.highestOneBit(virtualMemorySize);
                virtualMemoryBitSize = (int)(Math.log(virtualMemorySize)/Math.log(2));
                if(physicalPageSize!=physicalSizeCheck){
                    mEdtPhysicalPageSize.setError("Physical Memory size should be power of 2");
                }else if(virtualMemorySize!=virtualSizeCheck){
                    mEdtVirtualMemorySize.setError("Virtual Memory size should be power of 2");
                }else {
                    mTxtInstUpdates.setText("Information:\n" +
                            "Offset ="+offsetBits+" bits\n"+
                            "Instruction Length = log2("+virtualMemorySize+") = "+virtualMemoryBitSize+" bits\n"+
                            "Physical Page Rows = "+physicalPageSize+"/2^"+offsetBits+" = "+(physicalPageSize)/(int)Math.pow(2,offsetBits)+" rows\n"+
                            "Page Table Rows = "+virtualMemorySize+"/2^"+offsetBits+" = "+(virtualMemorySize)/(int)Math.pow(2,offsetBits)+" rows\n"+
                            "TLB Rows = "+tlbEntries+" rows\n"+
                            "Please submit Instruction.");
                    tlbrecords = new TLBRecord[tlbEntries];
                    for(int i=0;i<tlbEntries;i++){
                        tlbrecords[i] = new TLBRecord(i,"-","-");
                    }
                    physicalMemoryRecords = new PhysicalMemoryRecord[(physicalPageSize)/(int)Math.pow(2,offsetBits)];
                    for(int i=0;i<physicalMemoryRecords.length;i++){
                        physicalMemoryRecords[i] = new PhysicalMemoryRecord(Integer.toHexString(i).toUpperCase(),"-");
                    }
                    pageTableRecords = new PageTableRecord[(virtualMemorySize)/(int)Math.pow(2,offsetBits)];
                    for(int i=0;i<pageTableRecords.length;i++){
                        pageTableRecords[i] = new PageTableRecord(Integer.toHexString(i).toUpperCase(),"0","-");
                    }
                    updateTLBTable(tlbrecords);
                    updatePhysicalMemory(physicalMemoryRecords);
                    updatePageTable(pageTableRecords);
                    tlbCurrentCounter=0;
                    phyCurrentCounter=0;
                    mBtnGetRandom.setEnabled(true);
                    mBtnInstSubmit.setEnabled(true);
                }
            }
        });

        mBtnGetRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressInstruction =  (int)(Math.random()*(virtualMemorySize)+1);
                mEdtAddress.setText(Integer.toHexString(addressInstruction));
            }
        });

        mBtnInstSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressInstruction=Integer.parseInt(mEdtAddress.getText().toString(),16);
                String binaryInstruction = String
                        .format("%"+ virtualMemoryBitSize+"s",Integer.toBinaryString(addressInstruction))
                        .replace(' ','0');
                currentPageBinary = binaryInstruction.substring(0,binaryInstruction.length()-offsetBits);
                currentOffsetBinary = binaryInstruction.substring(binaryInstruction.length()-offsetBits,binaryInstruction.length());
                mTxtInstUpdates.setText("Information:" +
                        "\nThe instruction has been converted from hex to binary and allocated to tag, index, and offset respectively");
                stepNumber = 2;
                updateInstructionTable(currentPageBinary,currentOffsetBinary,binaryInstruction.length()-offsetBits,offsetBits);
                mBtnFastForward.setEnabled(true);
                mBtnNextStep.setEnabled(true);

            }
        });

        mBtnFastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                while(stepNumber<=6){
                    executeInstruction();
                }
                mBtnNextStep.setEnabled(false);
            }
        });
        mBtnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    executeInstruction();
            }
        });

        return root;
    }

    public void  executeInstruction(){
        if(stepNumber==2){
            //Search in TLB
            mTxtInstUpdates.setText("Information:" +
                    "\nIndex requested will be searched in whole TLB");
            tlbhit = searchTLB();
            stepNumber++;
        } else if(stepNumber==3){
            //If item found in TLB, update status
            if(tlbhit){
                mTxtInstUpdates.setText("Information:" +
                        "\nValid page is found in the TLB. Frame and Offset is updated.");
                stepNumber=6;
            } else {
                mTxtInstUpdates.setText("Information:" +
                        "\nThere is no valid page in TLB.");
                stepNumber++;
            }
        } else if(stepNumber==4){
            //Search in Page Table
            mTxtInstUpdates.setText("Information:" +
                    "\nPage is continue to be searched in Page Table.");
            pagetablehit = searchPageTable();
            stepNumber++;
        } else if(stepNumber==5){
            //Update Status
            if(pagetablehit){
                mTxtInstUpdates.setText("Information:" +
                        "\nValid page is found in the Page Table.");
                stepNumber=6;
            } else {
                mTxtInstUpdates.setText("Information:" +
                        "\nPage requested is not found in Page Table.\n Data will be loaded from Secondary Memory.\n TLB, Page Table and Physical Memory is updated accordingly");
                stepNumber++;
            }
        } else if(stepNumber==6){
            //Store in Table if item not found
            if(!tlbhit){
                if(!pagetablehit){
                    //update tlb,physical memory and page table
                    tlbrecords[tlbCurrentCounter%tlbEntries]= new TLBRecord(tlbCurrentCounter%tlbEntries,currentPageBinary,Integer.toString(tlbCurrentCounter%tlbEntries));
                    tlbCurrentCounter++;
                    updateTLBTable(tlbrecords);
                    int phyMemoryRecordsCount = (physicalPageSize)/(int)Math.pow(2,offsetBits);
                    physicalMemoryRecords[phyCurrentCounter%phyMemoryRecordsCount] = new PhysicalMemoryRecord(Integer.toHexString(phyCurrentCounter%phyMemoryRecordsCount),"Blocks "+currentPageBinary+" Words: 0 - "+ Integer.toString((int)Math.pow(2,offsetBits)-1));
                    updatePhysicalMemory(physicalMemoryRecords);

                    int index = Integer.parseInt(currentPageBinary,2);
                    pageTableRecords[index]=new PageTableRecord(pageTableRecords[index].getIndex(),"1",Integer.toHexString(phyCurrentCounter%phyMemoryRecordsCount));
                    phyCurrentCounter++;
                    updatePageTable(pageTableRecords);
                } else {
                    mTxtInstUpdates.setText("Information:" +
                            "\nPage requested is found in Page Table. Let's fetch the data from Physical Memory. Page is updated in TLB as well.");
                    tlbrecords[tlbCurrentCounter%tlbEntries]= new TLBRecord(tlbCurrentCounter%tlbEntries,currentPageBinary,Integer.toString(tlbCurrentCounter%tlbEntries));
                    tlbCurrentCounter++;
                    updateTLBTable(tlbrecords);
                }
            }
            mTxtInstUpdates.setText("Information:" +
                    "\nThe cycle has been completed. Please submit another instructions");
            stepNumber++;
        }
    }

    public boolean searchTLB(){
        for(TLBRecord tlbRecord:tlbrecords){
            if(tlbRecord.getVirtualPage().equalsIgnoreCase((currentPageBinary))){
                return true;
            }
        }
        return false;
    }

    public boolean searchPageTable(){
        Log.d(TAG, "searchPageTable: "+pageTableRecords);
        Log.d(TAG, "searchPageTable:  pagetablehit "+pagetablehit);
        for(PageTableRecord pageTableRecord:pageTableRecords){
            if(pageTableRecord.getIndex().equalsIgnoreCase(Integer.toHexString(Integer.parseInt(currentPageBinary,2)))&&pageTableRecord.getValidBit().equals("1")){
                return true;
            }
            Log.d(TAG, "searchPageTable: pageTableRecord.getIndex()"+pageTableRecord.getIndex());
            Log.d(TAG, "searchPageTable: Integer.toHexString(Integer.parseInt(currentPageBinary)))"+Integer.toHexString(Integer.parseInt(currentPageBinary,2)));
        }
        return false;
    }
    @SuppressLint("ResourceType")
    public void updateInstructionTable(String currentPageBinary, String currentOffsetBinary, int pageBits, int offsetBits){
        mInstructionTableLayout.removeAllViews();
        int leftRowMargin = 0;
        int topRowMargin = 0;
        int rightRowMargin = 0;
        int bottomRowMargin = 0;
        final TextView tv = new TextView(getContext());
        tv.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setPadding(5, 15, 0, 15);
        tv.setText(currentPageBinary);
        tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
        final TextView tv2 = new TextView(getContext());
        tv2.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv2.setGravity(Gravity.CENTER_HORIZONTAL);
        tv2.setPadding(5, 15, 0, 15);
        tv2.setText(currentOffsetBinary);
        tv2.setBackgroundColor(Color.parseColor("#f0f0f0"));

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

        final TextView tv4 = new TextView(getContext());
        tv4.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv4.setGravity(Gravity.CENTER_HORIZONTAL);
        tv4.setPadding(5, 15, 0, 15);
        tv4.setText(pageBits+"");
        tv4.setTextColor(Color.parseColor("#000000"));
        final TextView tv5 = new TextView(getContext());
        tv5.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv5.setGravity(Gravity.CENTER_HORIZONTAL);
        tv5.setPadding(5, 15, 0, 15);
        tv5.setText(offsetBits+"");
        tv5.setTextColor(Color.parseColor("#000000"));

        final TableRow tr1 = new TableRow(getContext());
        tr1.setId(1);
        trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                bottomRowMargin);
        tr1.setPadding(0, 0, 0, 0);
        tr1.setLayoutParams(trParams);
        tr1.addView(tv4);
        tr1.addView(tv5);

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

    public void updateTLBTable(TLBRecord tlbrecords[]){
        mTlbTableLayout.removeAllViews();
        TextView textSpacer = null;
        int leftRowMargin = 0;
        int topRowMargin = 0;
        int rightRowMargin = 0;
        int bottomRowMargin = 0;
        for(int i = -1;i<tlbrecords.length;i++){
            if (i > -1) {

            }else {
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
                tv.setText("");
                tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
            } else {
//                    if(highLightRecord==i)
//                        tv.setBackgroundColor(Color.parseColor("#ffff00"));
//                    else
//                        tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv.setText(tlbrecords[i].getIndex()+"");
            }

            //Virtual Page
            final TextView tv2 = new TextView(getContext());
            tv2.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv2.setGravity(Gravity.LEFT);
            tv2.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv2.setText("Virtual Page#");
                tv2.setBackgroundColor(Color.parseColor("#f0f0f0"));
            } else {
//                    if(highLightRecord==i)
//                        tv.setBackgroundColor(Color.parseColor("#ffff00"));
//                    else
//                        tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv2.setText(tlbrecords[i].getVirtualPage());
            }

            //Physical Page
            final TextView tv3 = new TextView(getContext());
            tv3.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv3.setGravity(Gravity.LEFT);
            tv3.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv3.setText("Physical Page#");
                tv3.setBackgroundColor(Color.parseColor("#f0f0f0"));
            } else {
//                    if(highLightRecord==i)
//                        tv.setBackgroundColor(Color.parseColor("#ffff00"));
//                    else
//                        tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv3.setText(tlbrecords[i].getPhysicalPage());
            }

            //Table row
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
            mTlbTableLayout.addView(tr, trParams);
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
                mTlbTableLayout.addView(trSep, trParamsSep);
            }
        }

    }

    public void updatePhysicalMemory(PhysicalMemoryRecord physicalMemoryRecords[]){
        mPhysicalMemoryTableLayout.removeAllViews();
        TextView textSpacer = null;
        int leftRowMargin = 0;
        int topRowMargin = 0;
        int rightRowMargin = 0;
        int bottomRowMargin = 0;
        for(int i = -1;i<physicalMemoryRecords.length;i++){
            if (i > -1) {

            }else {
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
                tv.setText("Physical Page#");
                tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
            } else {
//                    if(highLightRecord==i)
//                        tv.setBackgroundColor(Color.parseColor("#ffff00"));
//                    else
//                        tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv.setText(physicalMemoryRecords[i].getPhysicalPage());
            }

            //Virtual Page
            final TextView tv2 = new TextView(getContext());
            tv2.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv2.setGravity(Gravity.LEFT);
            tv2.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv2.setText("Content");
                tv2.setBackgroundColor(Color.parseColor("#f0f0f0"));
            } else {
//                    if(highLightRecord==i)
//                        tv.setBackgroundColor(Color.parseColor("#ffff00"));
//                    else
//                        tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv2.setText(physicalMemoryRecords[i].getContent());
            }

            //Table row
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
            mPhysicalMemoryTableLayout.addView(tr, trParams);
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
                mPhysicalMemoryTableLayout.addView(trSep, trParamsSep);
            }
        }

    }

    public void updatePageTable(PageTableRecord pageTableRecords[]){
        mPageTableLayout.removeAllViews();
        TextView textSpacer = null;
        int leftRowMargin = 0;
        int topRowMargin = 0;
        int rightRowMargin = 0;
        int bottomRowMargin = 0;
        for(int i = -1;i<pageTableRecords.length;i++){
            if (i > -1) {

            }else {
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
                tv.setText("Index");
                tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
            } else {
//                    if(highLightRecord==i)
//                        tv.setBackgroundColor(Color.parseColor("#ffff00"));
//                    else
//                        tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv.setText(pageTableRecords[i].getIndex());
            }

            //Virtual Page
            final TextView tv2 = new TextView(getContext());
            tv2.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv2.setGravity(Gravity.LEFT);
            tv2.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv2.setText("Valid");
                tv2.setBackgroundColor(Color.parseColor("#f0f0f0"));
            } else {
//                    if(highLightRecord==i)
//                        tv.setBackgroundColor(Color.parseColor("#ffff00"));
//                    else
//                        tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv2.setText(pageTableRecords[i].getValidBit());
            }

            final TextView tv3 = new TextView(getContext());
            tv3.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv3.setGravity(Gravity.LEFT);
            tv3.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv3.setText("Physical Page#");
                tv3.setBackgroundColor(Color.parseColor("#f0f0f0"));
            } else {
//                    if(highLightRecord==i)
//                        tv.setBackgroundColor(Color.parseColor("#ffff00"));
//                    else
//                        tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv3.setText(pageTableRecords[i].getPhysicalPage());
            }
            //Table row
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
            mPageTableLayout.addView(tr, trParams);
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
                mPageTableLayout.addView(trSep, trParamsSep);
            }
        }

    }
}
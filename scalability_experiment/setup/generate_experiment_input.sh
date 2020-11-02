#!/bin/bash

# Script for generating the experiment input for the iobserve-analysis scalability experiment.
# Function generates the all lines of an event.
function generateInput () {
    line_count=$1
    event_id=$2
    user_id=$3
    call=$4
    file_path=$5

    # Templates for first second and third line of each event
    # An event is made up of a TraceMetadata record($1), BeforeOperationObjectInterfaceEvent($4) and AfterOperationObjectEvent($6)
    # $1;LoggingTimestamp;TraceId;ThreadId;SessionId;HostName;ParentTraceId;ParentOrderId;
    # $4;LoggingTimestamp;Timestamp;TraceId;OrderIndex;OperationSignature;ClassSignature;ObjectId;_Interface
    # $6;LoggingTimestamp;Timestamp;TraceId;OrderIndex;OperationSignature;ClassSignature;ObjectId

    # X -> timestamps/line count; Y -> TraceId/increasing Event ID; Z --> SessionId/UserID; CALL -> OperationSignature;ClassSignature combined
    first_line=$'$1;X;Y;00;Z;store-1;-1;-1 '
    first_line="$(echo ${first_line} | sed -e "s/X/${line_count}/g")"
    first_line="$(echo ${first_line} | sed -e "s/Y/${event_id}/g")"
    first_line="$(echo ${first_line} | sed -e "s/Z/${user_id}/g")"
    echo $first_line >> $file_path
    line_count=$((line_count+1))
    second_line=$'$4;X;X;Y;0;CALL;000000000;[] '
    second_line="$(echo ${second_line} | sed -e "s/X/${line_count}/g")"
    second_line="$(echo ${second_line} | sed -e "s/Y/${event_id}/g")"
    second_line="$(echo ${second_line} | sed -e "s/CALL/${call}/g")"
    echo $second_line >> $file_path
    line_count=$((line_count+1))
    third_line=$'$6;X;X;Y;1;CALL;000000000 '
    third_line="$(echo "${third_line}" | sed -e "s/X/${line_count}/g")"
    third_line="$(echo "${third_line}" | sed -e "s/Y/${event_id}/g")"
    third_line="$(echo "${third_line}" | sed -e "s/CALL/${call}/g")"
    echo $third_line >> $file_path
    line_count=$((line_count+1))
}

# Function creates the folder and the file path of each explicit setup.
function createFolderAndFilePath () {
    max_events=$1
    path_base=$2
    experiment_config=$3

    # Create path to file
    experiment_config_name="$(echo ${experiment_config} | sed -e "s/X/${max_events}/g")"
    path="${path_base}\\${experiment_config}\\${experiment_config_name}\\log"
    mkdir -p $path

    # Copy kieker.map into log folder
    cp "${PWD}\\kieker.map" "$path"

    # Returning the full path
    full_path="${path}\\${experiment_config_name}.dat"
    echo $full_path
}

# Function that initiates the generation of an explicit equal_events_X_users setup.
function generateInputEqualEvents () {
    max_events=$1
    path_base=$2
    # Call createFolderAndFilePath and get the file path as return
    file_path="$(createFolderAndFilePath $max_events $path_base "equal_events_X_users")"

    echo $file_path

    # Start iteration to create file contents
    line_count=0
    for (( event_id=1; event_id<=$max_events; event_id=event_id+1))
    do
        echo "Event " $event_id
        generateInput "$line_count" "$event_id" "$event_id" "public java.util.Set org.cocome.cloud.logic.webservice.cashdeskline.cashdesk.CashDesk.startSale(java.lang.String, long);org.cocome.cloud.logic.webservice.cashdeskline.cashdesk.CashDesk" "$file_path"
    done
}

# Function that initiates the generation of an explicit X_different_events_1_user setup.
function generateInputDifferentEvents () {
    max_events=$1
    path_base=$2
    # Call createFolderAndFilePath and get the file path as return
    file_path="$(createFolderAndFilePath $max_events $path_base "X_different_events_1_user")"

    echo $file_path

    # All possible calls, that can be chosen on random.
    possible_calls=(
    "public java.util.Set org.cocome.cloud.logic.webservice.cashdeskline.cashdesk.barcodescanner.BarcodeScanner.sendProductBarcode(java.lang.String,long,long);org.cocome.cloud.logic.webservice.cashdeskline.cashdesk.barcodescanner.BarcodeScanner"
    "public java.util.Set org.cocome.cloud.logic.webservice.cashdeskline.cashdesk.CashDesk.startSale(java.lang.String,long);org.cocome.cloud.logic.webservice.cashdeskline.cashdesk.CashDesk"
    "public java.util.Set org.cocome.cloud.logic.webservice.cashdeskline.cashdesk.CashDesk.selectPaymentMode(java.lang.String,long,java.lang.String);org.cocome.cloud.logic.webservice.cashdeskline.cashdesk.CashDesk"
    "public java.util.Set org.cocome.cloud.logic.webservice.cashdeskline.cashdesk.CashDesk.finishSale(java.lang.String,long);org.cocome.cloud.logic.webservice.cashdeskline.cashdesk.CashDesk"
    "public void org.cocome.cloud.logic.webservice.store.StoreManager.createStockItem(long,org.cocome.tradingsystem.inventory.application.store.ProductWithStockItemTO);org.cocome.cloud.logic.webservice.store.StoreManager"
    "public void org.cocome.tradingsystem.inventory.application.store.StoreServer.createStockItem(long,org.cocome.tradingsystem.inventory.application.store.ProductWithStockItemTO);org.cocome.tradingsystem.inventory.application.store.StoreServer"
    "public org.cocome.tradingsystem.inventory.application.store.ProductWithStockItemTO org.cocome.tradingsystem.inventory.application.store.StoreServer.getProductWithStockItem(long,long);org.cocome.tradingsystem.inventory.application.store.StoreServer"
    "public org.cocome.tradingsystem.inventory.application.store.StoreWithEnterpriseTO org.cocome.tradingsystem.inventory.application.store.StoreServer.getStore(long);org.cocome.tradingsystem.inventory.application.store.StoreServer"
    "public org.cocome.tradingsystem.inventory.data.enterprise.ITradingEnterprise org.cocome.tradingsystem.inventory.data.enterprise.StoreEnterpriseQueryProvider.queryEnterpriseByName(java.lang.String);org.cocome.tradingsystem.inventory.data.enterprise.StoreEnterpriseQueryProvider"
    "public org.cocome.tradingsystem.inventory.data.enterprise.IProduct org.cocome.tradingsystem.inventory.data.enterprise.StoreEnterpriseQueryProvider.queryProductByBarcode(long);org.cocome.tradingsystem.inventory.data.enterprise.StoreEnterpriseQueryProvider"
    "public void org.cocome.tradingsystem.inventory.data.enterprise.TradingEnterprise.initTradingEnterprise();org.cocome.tradingsystem.inventory.data.enterprise.TradingEnterprise"
    "public org.cocome.tradingsystem.inventory.data.store.IStockItem org.cocome.tradingsystem.inventory.data.store.EnterpriseStoreQueryProvider.queryStockItem(long,long);org.cocome.tradingsystem.inventory.data.store.EnterpriseStoreQueryProvider"
    "public org.cocome.tradingsystem.inventory.data.store.IStore org.cocome.tradingsystem.inventory.data.store.EnterpriseStoreQueryProvider.queryStoreById(long);org.cocome.tradingsystem.inventory.data.store.EnterpriseStoreQueryProvider"
    "public void org.cocome.tradingsystem.inventory.data.store.Store.initStore();org.cocome.tradingsystem.inventory.data.store.Store"
    "public org.cocome.tradingsystem.inventory.application.store.StoreWithEnterpriseTO org.cocome.tradingsystem.inventory.data.store.StoreDatatypesFactory.fillStoreWithEnterpriseTO(org.cocome.tradingsystem.inventory.data.store.IStore);org.cocome.tradingsystem.inventory.data.store.StoreDatatypesFactory"
    )

    # Start iteration to create file contents
    line_count=0
    for (( event_id=1; event_id<=$max_events; event_id=event_id+1))
    do
        echo "Event " $event_id
        # Seed random generator
        RANDOM=$$$(date +%s)
        # Pick random call
        call=${possible_calls[$RANDOM % ${#possible_calls[@]} ]}

        generateInput "$line_count" "$event_id" "0" "$call" "$file_path"
    done
}

# main function
# Create path and directory
generate_path="D:\iobserve-architectural-runtime-models-JSS-2020\scalability_experiment\setup\experiment_input"
mkdir -p $generate_path

# Iterate and call functions for generating for each setup (1,10,100,1000,10000,100000)
for (( c=1; c<100000; c=c*10 ))
do
    generateInputEqualEvents $c $generate_path 
    generateInputDifferentEvents $c $generate_path
done

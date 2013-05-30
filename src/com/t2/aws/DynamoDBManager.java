/*
 * Copyright 2010-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.t2.aws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.AttributeAction;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodb.model.CreateTableRequest;
import com.amazonaws.services.dynamodb.model.DeleteItemRequest;
import com.amazonaws.services.dynamodb.model.DeleteTableRequest;
import com.amazonaws.services.dynamodb.model.DescribeTableRequest;
import com.amazonaws.services.dynamodb.model.DescribeTableResult;
import com.amazonaws.services.dynamodb.model.GetItemRequest;
import com.amazonaws.services.dynamodb.model.GetItemResult;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.KeySchema;
import com.amazonaws.services.dynamodb.model.KeySchemaElement;
import com.amazonaws.services.dynamodb.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.services.dynamodb.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodb.model.ScalarAttributeType;
import com.amazonaws.services.dynamodb.model.ScanRequest;
import com.amazonaws.services.dynamodb.model.ScanResult;
import com.amazonaws.services.dynamodb.model.UpdateItemRequest;
import com.t2.dataouthandler.DataOutHandler;

public class DynamoDBManager {

	/*
	 * Creates a table with the following attributes:
	 * 
	 * Table name: testTableName Hash key: userNo type N Read Capacity Units: 10
	 * Write Capacity Units: 5
	 */
	public static void createTable() {

		AmazonDynamoDBClient ddb = DataOutHandler.sClientManager
				.ddb();

		KeySchemaElement kse = new KeySchemaElement().withAttributeName(
				"userNo").withAttributeType(ScalarAttributeType.N);
		KeySchema ks = new KeySchema().withHashKeyElement(kse);
		ProvisionedThroughput pt = new ProvisionedThroughput()
				.withReadCapacityUnits(10l).withWriteCapacityUnits(5l);

		CreateTableRequest request = new CreateTableRequest()
				.withTableName(PropertyLoader.getInstance().getTestTableName())
				.withKeySchema(ks).withProvisionedThroughput(pt);

		try {
			ddb.createTable(request);
		} catch (AmazonServiceException ex) {
			DataOutHandler.sClientManager
					.wipeCredentialsOnAuthError(ex);
		}
	}

	/*
	 * Retrieves the table description and returns the table status as a string.
	 */
	public static String getTestTableStatus() {

		try {
			AmazonDynamoDBClient ddb = DataOutHandler.sClientManager
					.ddb();

			DescribeTableRequest request = new DescribeTableRequest()
					.withTableName(PropertyLoader.getInstance()
							.getTestTableName());
			DescribeTableResult result = ddb.describeTable(request);

			String status = result.getTable().getTableStatus();
			return status == null ? "" : status;

		} catch (ResourceNotFoundException e) {
			return "";
		} catch (AmazonServiceException ex) {
			DataOutHandler.sClientManager
					.wipeCredentialsOnAuthError(ex);
		}

		return "";
	}

	/*
	 * Inserts ten users with userNo from 1 to 10 and random names.
	 */
	public static void insertUsers() {
		AmazonDynamoDBClient ddb = DataOutHandler.sClientManager
				.ddb();

		try {
			for (int i = 1; i <= 10; i++) {

				HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();

				AttributeValue userNo = new AttributeValue().withN(String
						.valueOf(i));
				item.put("userNo", userNo);

				AttributeValue firstName = new AttributeValue().withS(Constants
						.getRandomName());
				item.put("firstName", firstName);

				AttributeValue lastName = new AttributeValue().withS(Constants
						.getRandomName());
				item.put("lastName", lastName);

				PutItemRequest request = new PutItemRequest().withTableName(
						PropertyLoader.getInstance().getTestTableName())
						.withItem(item);

				ddb.putItem(request);
			}
		} catch (AmazonServiceException ex) {
			DataOutHandler.sClientManager
					.wipeCredentialsOnAuthError(ex);
		}
	}

	/*
	 * Scans the table and returns the list of users.
	 */
	public static List<Map<String, AttributeValue>> getUserList() {

		AmazonDynamoDBClient ddb = DataOutHandler.sClientManager
				.ddb();

		ScanRequest request = new ScanRequest();
		request.setTableName(PropertyLoader.getInstance().getTestTableName());

		try {
			ScanResult result = ddb.scan(request);
			Log.i("BFDemo", result.toString());
			return result.getItems();

		} catch (AmazonServiceException ex) {
			DataOutHandler.sClientManager
					.wipeCredentialsOnAuthError(ex);
		}

		return null;
	}

	/*
	 * Retrieves all of the attribute/value pairs for the specified user.
	 */
	public static Map<String, AttributeValue> getUserInfo(int userNo) {

		AmazonDynamoDBClient ddb = DataOutHandler.sClientManager
				.ddb();

		AttributeValue userNoAttr = new AttributeValue().withN(String
				.valueOf(userNo));
		Key primaryKey = new Key().withHashKeyElement(userNoAttr);
		GetItemRequest request = new GetItemRequest().withTableName(
				PropertyLoader.getInstance().getTestTableName()).withKey(
				primaryKey);

		try {
			GetItemResult result = ddb.getItem(request);
			return result.getItem();

		} catch (AmazonServiceException ex) {
			DataOutHandler.sClientManager
					.wipeCredentialsOnAuthError(ex);
		}

		return null;
	}

	/*
	 * Updates one attribute/value pair for the specified user.
	 */
	public static void updateAttributeStringValue(String value, String key,
			AttributeValue targetValue) {

		AmazonDynamoDBClient ddb = DataOutHandler.sClientManager
				.ddb();

		AttributeValue av = new AttributeValue().withS(value);
		AttributeValueUpdate avu = new AttributeValueUpdate().withValue(av)
				.withAction(AttributeAction.PUT);
		Key primaryKey = new Key().withHashKeyElement(targetValue);
		HashMap<String, AttributeValueUpdate> updates = new HashMap<String, AttributeValueUpdate>();
		updates.put(key, avu);

		UpdateItemRequest request = new UpdateItemRequest()
				.withTableName(PropertyLoader.getInstance().getTestTableName())
				.withKey(primaryKey).withAttributeUpdates(updates);

		try {
			ddb.updateItem(request);
		} catch (AmazonServiceException ex) {
			DataOutHandler.sClientManager
					.wipeCredentialsOnAuthError(ex);
		}
	}

	/*
	 * Deletes the specified user and all of its attribute/value pairs.
	 */
	public static void deleteUser(AttributeValue targetValue) {

		AmazonDynamoDBClient ddb = DataOutHandler.sClientManager
				.ddb();

		Key primaryKey = new Key().withHashKeyElement(targetValue);
		DeleteItemRequest request = new DeleteItemRequest().withTableName(
				PropertyLoader.getInstance().getTestTableName()).withKey(
				primaryKey);
		try {
			ddb.deleteItem(request);
		} catch (AmazonServiceException ex) {
			DataOutHandler.sClientManager
					.wipeCredentialsOnAuthError(ex);
		}
	}

	/*
	 * Deletes the test table and all of its users and their attribute/value
	 * pairs.
	 */
	public static void cleanUp() {

		AmazonDynamoDBClient ddb = DataOutHandler.sClientManager
				.ddb();

		DeleteTableRequest request = new DeleteTableRequest()
				.withTableName(PropertyLoader.getInstance().getTestTableName());
		try {
			ddb.deleteTable(request);

		} catch (AmazonServiceException ex) {
			DataOutHandler.sClientManager
					.wipeCredentialsOnAuthError(ex);
		}
	}
}

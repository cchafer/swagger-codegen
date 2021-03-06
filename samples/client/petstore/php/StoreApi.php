<?php
/**
 *  Copyright 2015 Reverb Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 *
 * NOTE: This class is auto generated by the swagger code generator program. Do not edit the class manually.
 */
class StoreApi {

  function __construct($apiClient) {
    $this->apiClient = $apiClient;
  }

  
  /**
   * getInventory
   *
   * Returns pet inventories by status
   *
   * @return map[string,int]
   */
   public function getInventory() {

      // parse inputs
      $resourcePath = "/store/inventory";
      $resourcePath = str_replace("{format}", "json", $resourcePath);
      $method = "GET";
      $queryParams = array();
      $headerParams = array();
      $formParams = array();
      $_header_accept = 'application/json, application/xml';
      if ($_header_accept !== '') {
        $headerParams['Accept'] = $_header_accept;
      }
      $_header_content_type = array();
      $headerParams['Content-Type'] = count($_header_content_type) > 0 ? $_header_content_type[0] : 'application/json';

      
      
      
      
      

      // for HTTP post (form)
      $body = $body ?: $formParams;

      if (strpos($headerParams['Content-Type'], "application/x-www-form-urlencoded") > -1) {
        $body = http_build_query($body);
      }

      // make the API Call
      $response = $this->apiClient->callAPI($resourcePath, $method,
                                            $queryParams, $body,
                                            $headerParams);

      if(! $response) {
        return null;
      }

  		$responseObject = $this->apiClient->deserialize($response,
  		                                                'map[string,int]');
  		return $responseObject;
  }
  
  /**
   * placeOrder
   *
   * Place an order for a pet
   *
   * @param Order $body order placed for purchasing the pet (required)
   * @return Order
   */
   public function placeOrder($body) {

      // parse inputs
      $resourcePath = "/store/order";
      $resourcePath = str_replace("{format}", "json", $resourcePath);
      $method = "POST";
      $queryParams = array();
      $headerParams = array();
      $formParams = array();
      $_header_accept = 'application/json, application/xml';
      if ($_header_accept !== '') {
        $headerParams['Accept'] = $_header_accept;
      }
      $_header_content_type = array();
      $headerParams['Content-Type'] = count($_header_content_type) > 0 ? $_header_content_type[0] : 'application/json';

      
      
      
      
      // body params
      $body = null;
      if (isset($body)) {
        $body = $body;
      }

      // for HTTP post (form)
      $body = $body ?: $formParams;

      if (strpos($headerParams['Content-Type'], "application/x-www-form-urlencoded") > -1) {
        $body = http_build_query($body);
      }

      // make the API Call
      $response = $this->apiClient->callAPI($resourcePath, $method,
                                            $queryParams, $body,
                                            $headerParams);

      if(! $response) {
        return null;
      }

  		$responseObject = $this->apiClient->deserialize($response,
  		                                                'Order');
  		return $responseObject;
  }
  
  /**
   * getOrderById
   *
   * Find purchase order by ID
   *
   * @param string $order_id ID of pet that needs to be fetched (required)
   * @return Order
   */
   public function getOrderById($order_id) {

      // parse inputs
      $resourcePath = "/store/order/{orderId}";
      $resourcePath = str_replace("{format}", "json", $resourcePath);
      $method = "GET";
      $queryParams = array();
      $headerParams = array();
      $formParams = array();
      $_header_accept = 'application/json, application/xml';
      if ($_header_accept !== '') {
        $headerParams['Accept'] = $_header_accept;
      }
      $_header_content_type = array();
      $headerParams['Content-Type'] = count($_header_content_type) > 0 ? $_header_content_type[0] : 'application/json';

      
      
      // path params
      if($order_id !== null) {
        $resourcePath = str_replace("{" . "orderId" . "}",
                                    $this->apiClient->toPathValue($order_id), $resourcePath);
      }
      
      

      // for HTTP post (form)
      $body = $body ?: $formParams;

      if (strpos($headerParams['Content-Type'], "application/x-www-form-urlencoded") > -1) {
        $body = http_build_query($body);
      }

      // make the API Call
      $response = $this->apiClient->callAPI($resourcePath, $method,
                                            $queryParams, $body,
                                            $headerParams);

      if(! $response) {
        return null;
      }

  		$responseObject = $this->apiClient->deserialize($response,
  		                                                'Order');
  		return $responseObject;
  }
  
  /**
   * deleteOrder
   *
   * Delete purchase order by ID
   *
   * @param string $order_id ID of the order that needs to be deleted (required)
   * @return void
   */
   public function deleteOrder($order_id) {

      // parse inputs
      $resourcePath = "/store/order/{orderId}";
      $resourcePath = str_replace("{format}", "json", $resourcePath);
      $method = "DELETE";
      $queryParams = array();
      $headerParams = array();
      $formParams = array();
      $_header_accept = 'application/json, application/xml';
      if ($_header_accept !== '') {
        $headerParams['Accept'] = $_header_accept;
      }
      $_header_content_type = array();
      $headerParams['Content-Type'] = count($_header_content_type) > 0 ? $_header_content_type[0] : 'application/json';

      
      
      // path params
      if($order_id !== null) {
        $resourcePath = str_replace("{" . "orderId" . "}",
                                    $this->apiClient->toPathValue($order_id), $resourcePath);
      }
      
      

      // for HTTP post (form)
      $body = $body ?: $formParams;

      if (strpos($headerParams['Content-Type'], "application/x-www-form-urlencoded") > -1) {
        $body = http_build_query($body);
      }

      // make the API Call
      $response = $this->apiClient->callAPI($resourcePath, $method,
                                            $queryParams, $body,
                                            $headerParams);

      
  }
  

}

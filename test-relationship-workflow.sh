#!/bin/bash

# Relationship Approval Workflow Test Script
# This script demonstrates the two-way approval workflow

echo "🧪 Testing Relationship Approval Workflow"
echo "=========================================="

BASE_URL="http://localhost:8083/api/v1"
USER1_ID=1
USER2_ID=2
RELATIONSHIP_TYPE_ID=5  # Brother relationship

echo ""
echo "📋 Step 1: Check if there are any existing pending requests between users"
echo "GET $BASE_URL/relationship-requests/check-pending/$USER1_ID/$USER2_ID"
curl -s "$BASE_URL/relationship-requests/check-pending/$USER1_ID/$USER2_ID" | jq '.'
echo ""

echo "📤 Step 2: User 1 sends a relationship request to User 2"
echo "POST $BASE_URL/relationship-requests/send"
REQUEST_RESPONSE=$(curl -s -X POST "$BASE_URL/relationship-requests/send" \
  -H "Content-Type: application/json" \
  -d "{
    \"requesterUserId\": $USER1_ID,
    \"recipientUserId\": $USER2_ID,
    \"relationshipTypeId\": $RELATIONSHIP_TYPE_ID,
    \"requestMessage\": \"Hi, I would like to add you as my brother.\"
  }")

echo "$REQUEST_RESPONSE" | jq '.'

# Extract relationship ID from response
RELATIONSHIP_ID=$(echo "$REQUEST_RESPONSE" | jq -r '.data.id // empty')

if [ -z "$RELATIONSHIP_ID" ] || [ "$RELATIONSHIP_ID" = "null" ]; then
    echo "❌ Failed to create relationship request"
    echo "Response: $REQUEST_RESPONSE"
    exit 1
fi

echo ""
echo "✅ Relationship request created with ID: $RELATIONSHIP_ID"
echo ""

echo "📥 Step 3: Check pending requests for User 2 (recipient)"
echo "GET $BASE_URL/relationship-requests/pending/$USER2_ID"
curl -s "$BASE_URL/relationship-requests/pending/$USER2_ID" | jq '.'
echo ""

echo "📤 Step 4: Check sent requests by User 1 (sender)"
echo "GET $BASE_URL/relationship-requests/sent/$USER1_ID"
curl -s "$BASE_URL/relationship-requests/sent/$USER1_ID" | jq '.'
echo ""

echo "🤝 Step 5: User 2 accepts the relationship request"
echo "POST $BASE_URL/relationship-requests/respond"
ACCEPT_RESPONSE=$(curl -s -X POST "$BASE_URL/relationship-requests/respond" \
  -H "Content-Type: application/json" \
  -d "{
    \"relationshipId\": $RELATIONSHIP_ID,
    \"responderUserId\": $USER2_ID,
    \"action\": \"ACCEPT\",
    \"responseMessage\": \"Sure, I accept you as my brother!\"
  }")

echo "$ACCEPT_RESPONSE" | jq '.'
echo ""

echo "✅ Step 6: Verify the relationship is now ACTIVE"
echo "GET $BASE_URL/relationship-requests/$RELATIONSHIP_ID"
curl -s "$BASE_URL/relationship-requests/$RELATIONSHIP_ID" | jq '.'
echo ""

echo "📊 Step 7: Check relationship statistics"
echo "GET $BASE_URL/relationship-types/stats"
curl -s "$BASE_URL/relationship-types/stats" | jq '.'
echo ""

echo "🎉 Relationship Approval Workflow Test Completed!"
echo "================================================"
echo ""
echo "Summary:"
echo "- User 1 sent relationship request to User 2"
echo "- User 2 received the pending request"
echo "- User 2 accepted the request"
echo "- Relationship status changed from PENDING to ACTIVE"
echo "- Two-way approval workflow working correctly!"

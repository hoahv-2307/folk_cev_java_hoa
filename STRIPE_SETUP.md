# Stripe Payment Integration Setup Guide

This guide explains how to set up Stripe payment gateway for credit card processing in the Foods App.

## Prerequisites

1. A Stripe account (sign up at https://stripe.com)
2. Stripe test API keys for development

## Getting Stripe API Keys

1. **Log in to your Stripe Dashboard**: https://dashboard.stripe.com/
2. **Navigate to Developers > API Keys**
3. **Copy your keys**:
   - **Publishable key** (starts with `pk_test_`) - Safe to use in frontend
   - **Secret key** (starts with `sk_test_`) - Keep secure, server-side only

## Configuration

### 1. Environment Variables

Add your Stripe keys to your `.env` file:

```env
# Stripe Payment Configuration
STRIPE_PUBLISHABLE_KEY=pk_test_your_actual_publishable_key_here
STRIPE_SECRET_KEY=sk_test_your_actual_secret_key_here
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret_here
```

⚠️ **Important**: 
- Never commit actual API keys to version control
- Use test keys (`pk_test_*`, `sk_test_*`) for development
- Use live keys (`pk_live_*`, `sk_live_*`) only in production

### 2. Test Cards

For testing payments, use Stripe's test card numbers:

- **Successful payment**: `4242424242424242`
- **Declined payment**: `4000000000000002`
- **Requires authentication**: `4000002500003155`

**Test card details**:
- Any future expiration date (e.g., 12/34)
- Any 3-digit CVC (e.g., 123)
- Any postal code (e.g., 12345)

## Features

### Payment Methods
- **Cash on Delivery**: Traditional payment method
- **Credit/Debit Cards**: Stripe-processed payments

### Card Processing Flow
1. User selects items and goes to checkout
2. Chooses "Credit Card" payment method
3. Enters card details using Stripe Elements (secure)
4. Payment is processed through Stripe
5. Order is created only after successful payment

### Security Features
- **PCI Compliance**: Card data never touches your server
- **Stripe Elements**: Secure, customizable card input fields
- **3D Secure**: Automatic authentication for eligible cards
- **Fraud Detection**: Built-in Stripe Radar protection

## API Endpoints

### Payment Endpoints
- `GET /api/payments/config` - Get Stripe publishable key
- `POST /api/payments/create-payment-intent` - Create payment intent
- `POST /api/payments/confirm/{paymentIntentId}` - Confirm payment
- `POST /api/payments/cancel/{paymentIntentId}` - Cancel payment intent

### Enhanced Checkout
- `POST /checkout` - Process order with payment method selection

## Database Changes

The `orders` table now includes:
- `payment_method`: "cash" or "card"
- `payment_intent_id`: Stripe payment intent ID
- `payment_status`: "pending", "completed", "failed", "canceled"

## Troubleshooting

### Common Issues

1. **"Invalid API key"**
   - Check that your API key is correct and starts with `sk_test_`
   - Ensure no extra spaces in your `.env` file

2. **"Card declined"**
   - Use test card numbers for development
   - Check that the card details are valid

3. **Payment not processing**
   - Check browser console for JavaScript errors
   - Verify Stripe.js is loaded correctly
   - Ensure internet connection is stable

### Testing Checklist

- [ ] Cash on delivery orders work correctly
- [ ] Credit card payments process successfully
- [ ] Failed payments are handled gracefully
- [ ] Order status reflects payment status
- [ ] Cart is cleared only after successful payment

## Production Deployment

Before going live:

1. **Replace test keys** with live keys in production `.env`
2. **Set up webhooks** for payment event handling (optional)
3. **Test thoroughly** with real bank cards
4. **Enable HTTPS** (required for live payments)
5. **Set up proper error monitoring**

## Security Best Practices

- Never log or store actual card numbers
- Use environment variables for all API keys
- Enable webhook signature verification
- Implement proper error handling
- Monitor failed payment attempts
- Set up fraud alerts in Stripe Dashboard

## Support

- **Stripe Documentation**: https://stripe.com/docs
- **Stripe Dashboard**: https://dashboard.stripe.com/
- **Test Data**: https://stripe.com/docs/testing

For application-specific issues, check the application logs and ensure all environment variables are properly set.
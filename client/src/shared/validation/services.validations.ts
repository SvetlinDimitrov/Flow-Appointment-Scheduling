import {z} from "zod";

export const serviceCreateUpdateValidations = z.object({
  name: z.string()
    .min(3, 'Name must be between 3 and 255 characters')
    .max(255, 'Name must be between 3 and 255 characters'),
  description: z.string()
    .min(1, 'Description is mandatory'),
  duration: z.number()
    .min(1, 'Duration must be greater than 0')
    .nonnegative('Duration is mandatory'),
  price: z.number()
    .gt(0, 'Price must be greater than 0')
    .nonnegative('Price is mandatory'),
  workSpaceName: z.string().min(1, 'Place name is required'),
  availability: z.boolean(),
});
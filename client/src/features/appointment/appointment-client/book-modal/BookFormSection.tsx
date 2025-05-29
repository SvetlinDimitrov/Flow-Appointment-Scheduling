import {Avatar, Box, Button, TextField, Typography} from '@mui/material';
import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import MapComponent from '../../../../shared/core/map/MapComponent.tsx';
import {User} from '../../../../shared/models/user.types.ts';
import {Service} from '../../../../shared/models/service.types.ts';
import {DateTime, Duration} from 'luxon';
import {ChangeEvent} from "react";
import {AppointmentCreate} from "../../../../shared/models/appointment.types.ts";

interface FormData {
  from: string;
  to: string;
}

interface BookFormSectionProps {
  service: Service;
  staff: User;
  client: User;
  onSubmit: (data: AppointmentCreate) => void;
}

const getWorkingHour = (workingHour: DateTime | undefined): string => {
  return workingHour ? DateTime.fromISO(workingHour.toString()).toFormat('HH:mm') : '';
};

const createSchema = (beginWorkingHour: string, endWorkingHour: string) => z.object({
  from: z.string()
    .refine((val) => {
      const date = DateTime.fromISO(val);
      return date.toFormat('HH:mm') >= beginWorkingHour && date.toFormat('HH:mm') <= endWorkingHour;
    }, {
      message: `The date must be between ${DateTime.fromISO(beginWorkingHour).toFormat('hh:mm a')} and ${DateTime.fromISO(endWorkingHour).toFormat('hh:mm a')}`
    })
    .refine((val) => {
      const date = DateTime.fromISO(val);
      return date >= DateTime.now();
    }, {
      message: 'The date must not be in the past'
    })
    .refine((val) => {
      const date = DateTime.fromISO(val);
      return date.diff(DateTime.now(), 'minutes').minutes >= 30;
    }, {
      message: 'The date must be at least 30 minutes from now'
    }),
  to: z.string().optional()
    .refine((val) => {
      if (!val) return true;
      const date = DateTime.fromISO(val);
      return date.toFormat('HH:mm') <= endWorkingHour;
    }, {
      message: `The end date must be before ${DateTime.fromISO(endWorkingHour).toFormat('hh:mm a')}`
    }),
});

/**
 * This component is responsible for the form part. It has two dates: the startDate (from) and endDate (to).
 * The server requires only the startDate (from); the second date is just to help the user.
 * For the startDate, I need to ensure that the date must be in the future or in the present
 * because the backend will not accept it otherwise.
 * If the date is in the present, it's good, in my opinion, to have a 30-minute distance
 * between the current date and now. The server does not require that, but I added it for a better experience.
 */

const BookFormSection = ({ service, staff, onSubmit, client }: BookFormSectionProps) => {
  const beginWorkingHour = getWorkingHour(staff.staffDetails?.beginWorkingHour);
  const endWorkingHour = getWorkingHour(staff.staffDetails?.endWorkingHour);

  const schema = createSchema(beginWorkingHour, endWorkingHour);

  const { register, handleSubmit, watch, setValue, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: {
      from: '',
      to: '',
    }
  });

  const handleFromChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const newValue = e.target.value;
    setValue('from', newValue);
    const startDateTime = DateTime.fromISO(newValue);
    const endDateTime = startDateTime.plus({ minutes: Duration.fromISO(service.duration).as('minutes') });
    setValue('to', endDateTime.toISO()?.slice(0, 16) ?? '');
  };

  const handleFormSubmit = (data: FormData) => {

    const appointmentCreate: AppointmentCreate = {
      serviceId: service.id,
      clientEmail: client.email,
      staffEmail: staff.email,
      date: DateTime.fromISO(data.from).toFormat("yyyy-MM-dd'T'HH:mm:ss"),
    };

    onSubmit(appointmentCreate);
  };

  return (
    <Box flex={1}>
      <Typography
        variant="h5"
        textAlign={'center'}
      >
        You are about to participate in the {service.name} service, which lasts for {Duration.fromISO(service.duration).as('minutes')} min.
      </Typography>
      <form
        onSubmit={handleSubmit(handleFormSubmit)}
        style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '1rem',
        justifyContent: 'center',
        alignItems: 'center',
        }}
      >
        <TextField
          {...register('from')}
          size={'small'}
          label="from"
          type="datetime-local"
          fullWidth
          margin="normal"
          InputLabelProps={{
            shrink: true,
          }}
          error={!!errors.from}
          helperText={errors.from ? errors.from.message : 'Please select the date you want to book'}
          onChange={handleFromChange}
        />
        {watch('from') && (
          <TextField
            {...register('to')}
            size={'small'}
            label="to"
            type="datetime-local"
            fullWidth
            margin="normal"
            InputLabelProps={{
              shrink: true,
            }}
            disabled
            error={!!errors.to}
            helperText={errors.to ? errors.to.message : ''}
          />
        )}
        <Typography variant="subtitle1">
          Your mentor information:
        </Typography>
        <Box
          display="flex"
          alignItems="center"
        >
          <Avatar
            src={"/static/images/no-picture-found"}
            alt={`${staff.firstName} ${staff.lastName}`}
          />
          <Box ml={2}>
            <Typography variant="subtitle2">
              {staff.firstName} {staff.lastName}
            </Typography>
            <Typography variant="subtitle2">
              {staff.email}
            </Typography>
          </Box>
        </Box>
        <Typography variant="subtitle1">
          Location for the event can be found here:
        </Typography>
        <MapComponent
          position={[51.505, -0.09]}
          style={{height: '180px', width: '100%', margin: 'auto'}}
        />
        <Box mt={2}>
          <Typography variant="subtitle2">
            If you have any questions about the event, feel free to contact your mentor. Once you submit the form, we will
            reach out to you for confirmation.
          </Typography>
          <Button
            style={{marginTop: '1rem', width: '100%'}}
            type="submit"
            variant="contained"
            color="primary"
            fullWidth
          >
            Book
          </Button>
        </Box>
      </form>
    </Box>
  );
};

export default BookFormSection;
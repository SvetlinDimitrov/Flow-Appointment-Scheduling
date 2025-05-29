import {useMutation} from "@tanstack/react-query";
import {toast} from "react-toastify";
import {AppointmentCreate} from "../../../shared/models/appointment.types.ts";
import {queryClient} from "../../../utils/react-query/queryClient.ts";
import {createAppointment} from "../../../services/appintment-service.ts";
import {AxiosError} from "axios";

const useCreateAppointmentMutation = () => {

  return useMutation({
    mutationFn: (newAppointment: AppointmentCreate) =>
      createAppointment(newAppointment),
    onSuccess: () => {
      queryClient.invalidateQueries({queryKey: ['appointments']});
      toast.success("Appointment created successfully.");
    },
    onError: (error) => {
      if (error instanceof AxiosError) {
        if (error.response?.data?.title === "Appointment overlap") {
          toast.error("You or your mentor already have an appointment at this time. Please select another time.");
        } else {
          toast.error("Appointment creation failed. Please try again later.");
        }
      } else {
        toast.error("An unexpected error occurred. Please try again later.");
      }
    },
  });
};

export default useCreateAppointmentMutation;
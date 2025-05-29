import {useMutation, useQueryClient} from '@tanstack/react-query';
import {assignStaffToService} from '../../../services/service-service';
import {toast} from 'react-toastify';

const useAssignStaffToServiceMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({id, staffEmail}: { id: number; staffEmail: string | undefined }) => {
      if (!staffEmail) {
        return Promise.reject(new Error('Staff email is required'));
      }
      return assignStaffToService(id, staffEmail);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({queryKey: ['services']});
      queryClient.invalidateQueries({queryKey: ['users']});
      queryClient.invalidateQueries({queryKey: ['user']});
      toast.success('Staff assigned successfully.');
    },
  });
};

export default useAssignStaffToServiceMutation;
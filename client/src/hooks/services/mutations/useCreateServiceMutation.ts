import {useMutation, useQueryClient} from '@tanstack/react-query';
import {createService} from '../../../services/service-service';
import {ServiceDTO} from '../../../shared/models/api/services';
import {toast} from 'react-toastify';

const useCreateServiceMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({service}: { service: ServiceDTO}) => createService(service),
    onSuccess: () => {
      queryClient.invalidateQueries({queryKey: ['services']});
      toast.success('Service created successfully.');
    },
  });
};

export default useCreateServiceMutation;
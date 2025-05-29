import {Box, Button, Typography} from '@mui/material';
import {ToolbarProps} from 'react-big-calendar';
import {NavigationAction, ViewOption} from "../../../../shared/models/react-big-calendar.ts";
import {useIsFetching} from '@tanstack/react-query';

const AdminStaffCustomToolbar = ({label, onNavigate, onView, view}: ToolbarProps) => {
  const isFetching = useIsFetching() > 0;

  return (
    <Box
      display="flex"
      justifyContent="space-between"
      mb={2}
      gap={2}
      sx={{
        flexDirection: {
          xs: 'column',
          md: 'row',
        },
        gap: 1,
      }}>
      <Box
        display={'flex'}
        gap={1}
        justifyContent={'center'}
      >
        <Button
          variant={'outlined'}
          color={'primary'}
          size={'small'}
          onClick={() => onNavigate(NavigationAction.TODAY)}
          disabled={isFetching}
        >
          Today
        </Button>
        <Button
          variant={'outlined'}
          color={'primary'}
          size={'small'}
          onClick={() => onNavigate(NavigationAction.PREV)}
          disabled={isFetching}
        >
          Previous
        </Button>
        <Button
          variant={'outlined'}
          color={'primary'}
          size={'small'}
          onClick={() => onNavigate(NavigationAction.NEXT)}
          disabled={isFetching}
        >
          Next
        </Button>
      </Box>
      <Typography
        variant={'subtitle1'}
        textAlign={'center'}
      >
        {label}
      </Typography>
      <Box
        display={'flex'}
        gap={1}
        justifyContent={'center'}
      >
        <Button
          variant={'outlined'}
          color={'primary'}
          size={'small'}
          onClick={() => onView(ViewOption.DAY)}
          disabled={view === ViewOption.DAY || isFetching}
        >
          Day
        </Button>
        <Button
          variant={'outlined'}
          color={'primary'}
          size={'small'}
          onClick={() => onView(ViewOption.WEEK)}
          disabled={view === ViewOption.WEEK || isFetching}
        >
          Week
        </Button>

        <Button
          variant={'outlined'}
          color={'primary'}
          size={'small'}
          onClick={() => onView(ViewOption.MONTH)}
          disabled={view === ViewOption.MONTH || isFetching}
        >
          Month
        </Button>
        <Button
          variant={'outlined'}
          color={'primary'}
          size={'small'}
          onClick={() => onView(ViewOption.AGENDA)}
          disabled={view === ViewOption.AGENDA || isFetching}
        >
          Agenda
        </Button>
      </Box>
    </Box>
  );
};

export default AdminStaffCustomToolbar;
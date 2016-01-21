package org.masonapps.robogui;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommandListFragment extends Fragment {


    private RecyclerView recyclerView;

    public CommandListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_command_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                add();
                break;
            case R.id.action_clear:
                clear();
                break;
            case R.id.action_run:
                run();
                break;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_command_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.commandRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        final CommandListAdapter adapter = new CommandListAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void add() {
        AppCompatDialogFragment dialogFragment = new AppCompatDialogFragment(){
            int mag = 20;
            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.dialog_add_command, container, false);
                final TextView magText = (TextView) view.findViewById(R.id.magText);
                final Spinner spinner = (Spinner) view.findViewById(R.id.actionSpinner);
                spinner.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, MoveCommand.ACTION_NAMES));
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String units;
                        if(position == MoveCommand.ACTION_MOVE_FORWARD || position == MoveCommand.ACTION_MOVE_BACKWARD)
                            units = "cm";
                        else if(position == MoveCommand.ACTION_TURN_LEFT || position == MoveCommand.ACTION_TURN_RIGHT)
                            units = "deg";
                        else
                            units = "sec";
                        magText.setText(String.format("%d %s", mag, units));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                magText.setText(String.valueOf(mag));
                view.findViewById(R.id.magDownBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mag -= 5;
                        mag = Math.max(mag, 5);
                        final int position = spinner.getSelectedItemPosition();
                        String units;
                        if(position == MoveCommand.ACTION_MOVE_FORWARD || position == MoveCommand.ACTION_MOVE_BACKWARD)
                            units = "cm";
                        else if(position == MoveCommand.ACTION_TURN_LEFT || position == MoveCommand.ACTION_TURN_RIGHT)
                            units = "deg";
                        else
                            units = "sec";
                        magText.setText(String.format("%d %s", mag, units));
                    }
                });
                view.findViewById(R.id.magUpBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mag += 5;
                        final int position = spinner.getSelectedItemPosition();
                        String units;
                        if(position == MoveCommand.ACTION_MOVE_FORWARD || position == MoveCommand.ACTION_MOVE_BACKWARD)
                            units = "cm";
                        else if(position == MoveCommand.ACTION_TURN_LEFT || position == MoveCommand.ACTION_TURN_RIGHT)
                            units = "deg";
                        else
                            units = "sec";
                        magText.setText(String.format("%d %s", mag, units));
                    }
                });
                view.findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((CommandListAdapter)recyclerView.getAdapter()).add(new MoveCommand(spinner.getSelectedItemPosition(), mag));
                        dismiss();
                    }
                });
                return view;
            }

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                Dialog dialog = super.onCreateDialog(savedInstanceState);
                dialog.setTitle("Add Move Command");
                return dialog;
            }
        };
        dialogFragment.show(getChildFragmentManager(), null);
    }

    private void clear() {
        ((CommandListAdapter)recyclerView.getAdapter()).clear();
    }

    private void run() {
        
    }

    private static class CommandListAdapter extends RecyclerView.Adapter<CommandListAdapter.CommandViewHolder>{
        
        private ArrayList<MoveCommand> list = new ArrayList<>();
        
        public CommandListAdapter() {
        }

        @Override
        public CommandViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CommandViewHolder(this, LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_command, parent, false));
        }

        @Override
        public void onBindViewHolder(CommandViewHolder holder, int position) {
            holder.bind(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void add(MoveCommand command) {
            list.add(command);
            notifyItemInserted(list.size() - 1);
        }

        public void clear() {
            final int n = list.size();
            list.clear();
            notifyItemRangeRemoved(0, n);
        }

        static class CommandViewHolder extends RecyclerView.ViewHolder {
            private final CommandListAdapter adapter;
            private final TextView actionText;
            private final TextView magnitudeText;

            public CommandViewHolder(CommandListAdapter adapter, View itemView) {
                super(itemView);
                this.adapter = adapter;
                actionText = (TextView) itemView.findViewById(R.id.actionText);
                magnitudeText = (TextView) itemView.findViewById(R.id.magnitudeText);
                itemView.findViewById(R.id.deleteBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int position = getAdapterPosition();
                        CommandViewHolder.this.adapter.list.remove(position);
                        CommandViewHolder.this.adapter.notifyItemRemoved(position);
                    }
                });
            }
            
            void bind(MoveCommand command){
                final int action = command.getAction();
                actionText.setText(MoveCommand.actionName(action));
                String units;
                if(action == MoveCommand.ACTION_MOVE_FORWARD || action == MoveCommand.ACTION_MOVE_BACKWARD)
                    units = "cm";
                else if(action == MoveCommand.ACTION_TURN_LEFT || action == MoveCommand.ACTION_TURN_RIGHT)
                    units = "deg";
                else
                    units = "sec";

                magnitudeText.setText(String.format("%d %s", command.getMagnitude(), units));
            }
        }
    }
}

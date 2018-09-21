package br.edu.ifsp.scl.sdm.listacontatossdm.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import br.edu.ifsp.scl.sdm.listacontatossdm.R;
import br.edu.ifsp.scl.sdm.listacontatossdm.util.Configuracoes;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfiguracoesActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.armazenamentoRadioGroup)
    RadioGroup armaenamentoRadioGroup;

    private final int PERMISSAO_ARMAZENAMENTO_EXTERNO_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        ButterKnife.bind(this);

        getSupportActionBar().setSubtitle("Configurações");

        // Restaurando configuração existente
        switch (Configuracoes.getInstance().getTipoArmazenamento())
        {
            default:
            case (Configuracoes.ARMAZENAMENTO_INTERNO):
                armaenamentoRadioGroup.check(R.id.internoRadioButton);
                break;
            case (Configuracoes.ARMAZENAMENTO_EXTERNO):
                armaenamentoRadioGroup.check(R.id.externoRadioButton);
                break;
            case (Configuracoes.BANCO_DE_DADOS):
                armaenamentoRadioGroup.check(R.id.bdRadioButton);
                break;
        }

        armaenamentoRadioGroup.setOnCheckedChangeListener(((radioGroup,id)->{
                if (id == R.id.externoRadioButton ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        int pLeitura = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                        int pEscrita = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (pLeitura != PackageManager.PERMISSION_GRANTED || pEscrita != PackageManager.PERMISSION_GRANTED)
                        {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSAO_ARMAZENAMENTO_EXTERNO_REQUEST_CODE);
                        }
                    }
                }
            })
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSAO_ARMAZENAMENTO_EXTERNO_REQUEST_CODE) {
            for (int permissao : grantResults) {
                if (permissao != PackageManager.PERMISSION_GRANTED) {
                    armaenamentoRadioGroup.check(Configuracoes.ARMAZENAMENTO_INTERNO);
                    Toast.makeText(this,"Permissões não concedidas!",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    @OnClick({R.id.salvarButton,R.id.cancelarButton})
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            default:
            case R.id.cancelarButton:
                finish();
                break;

            case R.id.salvarButton:
                int armazenamentoSelecionado = Configuracoes.getInstance().getTipoArmazenamento();
                switch (armaenamentoRadioGroup.getCheckedRadioButtonId())
                {
                    default:
                    case R.id.internoRadioButton:
                        armazenamentoSelecionado = Configuracoes.ARMAZENAMENTO_INTERNO;
                        break;
                    case R.id.externoRadioButton:
                        armazenamentoSelecionado = Configuracoes.ARMAZENAMENTO_EXTERNO;
                        break;
                    case R.id.bdRadioButton:
                        armazenamentoSelecionado = Configuracoes.BANCO_DE_DADOS;
                        break;

                }
                Configuracoes.getInstance().setTipoArmazenamento(armazenamentoSelecionado);
                break;
        }
    }
}

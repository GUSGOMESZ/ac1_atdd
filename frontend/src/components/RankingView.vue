<template>
  <div class="card">
    <h2>Resultado do Ranking</h2>

    <div class="winner-box" v-if="resultado.alunoVencedor">
      <div class="trophy">🏆</div>
      <div>
        <div class="winner-name">{{ resultado.alunoVencedor.nome }}</div>
        <div class="winner-prize">Ganhou: <strong>{{ resultado.cursoGanho?.nome }}</strong> ({{ resultado.cursoGanho?.tipo }})</div>
        <div class="notificacao">
          Notificação: {{ resultado.notificacaoEnviada ? 'Enviada' : 'Não enviada' }}
        </div>
      </div>
    </div>

    <div v-if="resultado.alunosVencedores?.length > 1" class="outros-vencedores">
      <strong>Outros vencedores:</strong>
      <span v-for="a in outrosVencedores" :key="a.id" class="badge">{{ a.nome }}</span>
    </div>

    <table v-if="resultado.ranking?.length">
      <thead>
        <tr>
          <th>#</th>
          <th>Aluno</th>
          <th>Pontuação</th>
          <th>Úteis</th>
          <th>Não Úteis</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(item, idx) in resultado.ranking"
          :key="item.id"
          :class="{ destaque: item.aluno?.id === resultado.alunoVencedor?.id }"
        >
          <td>{{ idx + 1 }}</td>
          <td>{{ item.aluno?.nome ?? `Aluno #${item.aluno?.id}` }}</td>
          <td><strong>{{ item.pontuacao }}</strong></td>
          <td>{{ item.comentariosContabilizados }}</td>
          <td>{{ item.comentariosNaoUteis }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ resultado: Object })

const outrosVencedores = computed(() =>
  props.resultado.alunosVencedores?.filter(a => a.id !== props.resultado.alunoVencedor?.id) ?? []
)
</script>

<style scoped>
.winner-box {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff8e1;
  border: 1px solid #ffe082;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.trophy {
  font-size: 2.5rem;
}

.winner-name {
  font-size: 1.3rem;
  font-weight: bold;
  color: #e65100;
}

.winner-prize {
  color: #555;
  margin-top: 4px;
}

.notificacao {
  font-size: 0.8rem;
  color: #777;
  margin-top: 4px;
}

.outros-vencedores {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 16px;
  font-size: 0.85rem;
}

.badge {
  background: #e8f5e9;
  color: #2e7d32;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

th {
  background: #f5f5f5;
  text-align: left;
  padding: 10px 12px;
  font-size: 0.8rem;
  color: #666;
  border-bottom: 2px solid #e0e0e0;
}

td {
  padding: 10px 12px;
  border-bottom: 1px solid #f0f0f0;
}

tr.destaque td {
  background: #fff3e0;
  font-weight: 600;
}

tr:hover td {
  background: #fafafa;
}
</style>
